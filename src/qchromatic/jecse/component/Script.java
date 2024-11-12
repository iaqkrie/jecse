package qchromatic.jecse.component;

import qchromatic.jecse.core.Component;

import java.util.function.Consumer;

public class Script extends Component {
	public Runnable init;
	public Consumer<Float> loop;
}
