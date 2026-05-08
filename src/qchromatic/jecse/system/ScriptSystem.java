package qchromatic.jecse.system;

import qchromatic.jecse.component.Script;
import qchromatic.jecse.core.Component;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.EntityQuery;
import qchromatic.jecse.core.System;

import java.util.*;

public class ScriptSystem extends System {
	private final List<Script> _scripts;
	private final Set<Script> _initializedScripts;
	private final Set<Script> _startedScripts;
	private final Map<Entity, List<Script>> _scriptsByEntity;
	private EntityQuery _scriptEntities;
	private boolean _running;

	public ScriptSystem () {
		super(0);
		_scripts = new ArrayList<>();
		_initializedScripts = new LinkedHashSet<>();
		_startedScripts = new LinkedHashSet<>();
		_scriptsByEntity = new LinkedHashMap<>();
		_running = false;
	}

	@Override
	public void init () {
		_scriptEntities = scene.queryInheritance(Script.class);
		_scripts.clear();
		_initializedScripts.clear();
		_startedScripts.clear();
		_scriptsByEntity.clear();

		for (Entity entity : _scriptEntities)
			registerEntity(entity);
	}

	@Override
	public void start () {
		_running = true;
		for (Script script : List.copyOf(_scripts))
			startScript(script);
	}

	@Override
	public void loop (float dtime) {
		for (Script script : List.copyOf(_scripts))
			script.loop(dtime);
	}

	@Override
	public void stop () {
		_running = false;
		for (Script script : List.copyOf(_startedScripts))
			stopScript(script);
	}

	@Override
	public void destroy () {
		for (Script script : List.copyOf(_scripts))
			destroyScript(script);

		_scripts.clear();
		_initializedScripts.clear();
		_startedScripts.clear();
		_scriptsByEntity.clear();
	}

	@Override
	public void onEntityAdded (Entity entity) {
		registerEntity(entity);
	}

	@Override
	public void onEntityRemoved (Entity entity) {
		unregisterEntity(entity, true);
	}

	@Override
	public void onComponentAdded(Entity entity, Component component) {
		if (component instanceof Script script)
			registerScript(entity, script);
	}

	@Override
	public void onComponentRemoved(Entity entity, Component component) {
		if (component instanceof Script script)
			unregisterScript(entity, script, true);
	}

	private void registerEntity (Entity entity) {
		for (Script script : entity.getComponentsInheritance(Script.class))
			registerScript(entity, script);
	}

	private void registerScript (Entity entity, Script script) {
		List<Script> entityScripts = _scriptsByEntity.computeIfAbsent(entity, ignored -> new ArrayList<>());
		if (entityScripts.contains(script)) return;

		entityScripts.add(script);
		_scripts.add(script);
		initScript(script);

		if (_running)
			startScript(script);
	}

	private void unregisterEntity (Entity entity, boolean destroy) {
		List<Script> entityScripts = _scriptsByEntity.remove(entity);
		if (entityScripts == null) return;

		for (Script script : List.copyOf(entityScripts))
			unregisterScript(entity, script, destroy);
	}

	private void unregisterScript (Entity entity, Script script, boolean destroy) {
		List<Script> entityScripts = _scriptsByEntity.get(entity);
		if (entityScripts != null) {
			entityScripts.remove(script);
			if (entityScripts.isEmpty())
				_scriptsByEntity.remove(entity);
		}

		_scripts.remove(script);

		if (destroy)
			destroyScript(script);
	}

	private void initScript (Script script) {
		if (_initializedScripts.add(script))
			script.init();
	}

	private void startScript (Script script) {
		initScript(script);
		if (_startedScripts.add(script))
			script.start();
	}

	private void stopScript (Script script) {
		if (_startedScripts.remove(script))
			script.stop();
	}

	private void destroyScript (Script script) {
		stopScript(script);

		if (_initializedScripts.remove(script))
			script.destroy();
	}
}
