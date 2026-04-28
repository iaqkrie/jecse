package qchromatic.jecse.graphics;

import qchromatic.jecse.core.Disposable;

import static org.lwjgl.opengl.GL30.*;

public class MeshGpuResource implements Disposable {
    private final int _vaoId;
    private final int _vboId;
    private final int _eboId;

    public MeshGpuResource (int vaoId, int vboId, int eboId) {
        _vaoId = vaoId;
        _vboId = vboId;
        _eboId = eboId;
    }

    public int vaoId () { return _vaoId; }

    public int vboId () { return _vboId; }

    public int eboId () { return _eboId; }

    @Override
    public void destroy() {
        glDeleteBuffers(_eboId);
        glDeleteBuffers(_vboId);
        glDeleteVertexArrays(_vaoId);
    }
}
