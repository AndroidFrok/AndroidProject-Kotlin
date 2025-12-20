package com.hjq.demo.widget

// WaveView.kt
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class WaveView(context: Context) : GLSurfaceView(context) {

    private val renderer: WaveRenderer

    init {
        // 设置OpenGL ES 2.0上下文
        setEGLContextClientVersion(2)

        // 创建渲染器
        renderer = WaveRenderer(context)
        setRenderer(renderer)

        // 设置渲染模式为连续渲染（用于动画）
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun onDestroy() {
        renderer.release()
    }
}

// 波浪渲染器
class WaveRenderer(private val context: Context) : GLSurfaceView.Renderer {

    // 波浪参数
    private var waveProgram = 0
    private var waveVerticesBufferId = 0
    private var waveIndicesBufferId = 0
    private var positionHandle = 0
    private var colorHandle = 0
    private var mvpMatrixHandle = 0
    private var timeHandle = 0

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var startTime: Long = 0
    private var vertexCount = 0

    // 波浪参数
    private val amplitude = 0.3f      // 振幅
    private val frequency = 1.5f      // 频率
    private val speed = 0.5f          // 速度

    // 顶点和索引数据
    private lateinit var vertices: FloatArray
    private lateinit var indices: ShortArray

    companion object {
        // 顶点着色器代码
        private const val VERTEX_SHADER = """
            uniform mat4 u_MVPMatrix;
            uniform float u_Time;
            attribute vec4 a_Position;
            attribute vec4 a_Color;
            varying vec4 v_Color;
            
            void main() {
                // 计算波浪效果
                float wave = sin(a_Position.x * 2.0 + u_Time * 1.5) * 0.1;
                vec4 pos = a_Position;
                pos.y = pos.y + wave;
                
                gl_Position = u_MVPMatrix * pos;
                v_Color = a_Color;
            }
        """

        // 片段着色器代码
        private const val FRAGMENT_SHADER = """
            precision mediump float;
            varying vec4 v_Color;
            
            void main() {
                gl_FragColor = v_Color;
            }
        """

        // 加载着色器
        fun loadShader(type: Int, shaderCode: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 设置背景颜色为深蓝色
        GLES20.glClearColor(0.1f, 0.2f, 0.3f, 1.0f)

        // 启用深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        // 启用混合（用于透明度）
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // 初始化时间
        startTime = System.currentTimeMillis()

        // 编译着色器并创建程序
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)

        waveProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(waveProgram, vertexShader)
        GLES20.glAttachShader(waveProgram, fragmentShader)
        GLES20.glLinkProgram(waveProgram)

        // 获取属性位置
        positionHandle = GLES20.glGetAttribLocation(waveProgram, "a_Position")
        colorHandle = GLES20.glGetAttribLocation(waveProgram, "a_Color")
        mvpMatrixHandle = GLES20.glGetUniformLocation(waveProgram, "u_MVPMatrix")
        timeHandle = GLES20.glGetUniformLocation(waveProgram, "u_Time")

        // 生成波浪网格
        generateWaveMesh()

        // 创建顶点缓冲
        createBuffers()
    }

    private fun generateWaveMesh() {
        val gridSize = 50  // 网格密度
        vertexCount = (gridSize + 1) * (gridSize + 1)

        vertices = FloatArray(vertexCount * 7) // 每个顶点: x, y, z, r, g, b, a

        var index = 0
        for (i in 0..gridSize) {
            for (j in 0..gridSize) {
                // 位置 (x, y, z)
                val x = (i.toFloat() / gridSize - 0.5f) * 2.0f
                val z = (j.toFloat() / gridSize - 0.5f) * 2.0f
                val y = 0.0f  // 基础高度

                vertices[index++] = x
                vertices[index++] = y
                vertices[index++] = z

                // 颜色 (基于位置)
                val r = 0.2f + 0.5f * (x + 1.0f) / 2.0f
                val g = 0.3f + 0.5f * (z + 1.0f) / 2.0f
                val b = 0.8f
                val a = 0.8f

                vertices[index++] = r
                vertices[index++] = g
                vertices[index++] = b
                vertices[index++] = a
            }
        }

        // 生成索引
        val indexCount = gridSize * gridSize * 6
        indices = ShortArray(indexCount)

        index = 0
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val topLeft = (i * (gridSize + 1) + j).toShort()
                val topRight = (i * (gridSize + 1) + j + 1).toShort()
                val bottomLeft = ((i + 1) * (gridSize + 1) + j).toShort()
                val bottomRight = ((i + 1) * (gridSize + 1) + j + 1).toShort()

                indices[index++] = topLeft
                indices[index++] = bottomLeft
                indices[index++] = topRight

                indices[index++] = topRight
                indices[index++] = bottomLeft
                indices[index++] = bottomRight
            }
        }
    }

    private fun createBuffers() {
        // 创建顶点缓冲
        val buffers = IntArray(2)
        GLES20.glGenBuffers(2, buffers, 0)
        waveVerticesBufferId = buffers[0]
        waveIndicesBufferId = buffers[1]

        // 绑定并上传顶点数据
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, waveVerticesBufferId)
        val vertexBuffer = java.nio.ByteBuffer.allocateDirect(vertices.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer.position(0)
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            vertexBuffer.capacity() * 4,
            vertexBuffer,
            GLES20.GL_STATIC_DRAW
        )

        // 绑定并上传索引数据
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, waveIndicesBufferId)
        val indexBuffer = java.nio.ByteBuffer.allocateDirect(indices.size * 2)
            .order(java.nio.ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(indices)
        indexBuffer.position(0)
        GLES20.glBufferData(
            GLES20.GL_ELEMENT_ARRAY_BUFFER,
            indexBuffer.capacity() * 2,
            indexBuffer,
            GLES20.GL_STATIC_DRAW
        )

        // 解绑
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()

        // 设置投影矩阵
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)

        // 设置相机位置
        Matrix.setLookAtM(
            viewMatrix, 0,
            0f, 0f, 3f,    // 相机位置
            0f, 0f, 0f,    // 观察点
            0f, 1f, 0f     // 上向量
        )

        // 计算MVP矩阵
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        // 清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // 计算时间
        val currentTime = System.currentTimeMillis()
        val elapsedTime = (currentTime - startTime) / 1000.0f

        // 使用波浪着色器程序
        GLES20.glUseProgram(waveProgram)

        // 传递MVP矩阵
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // 传递时间
        GLES20.glUniform1f(timeHandle, elapsedTime * speed)

        // 绑定顶点缓冲
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, waveVerticesBufferId)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, waveIndicesBufferId)

        // 设置顶点属性
        val stride = 7 * 4  // 7个浮点数 * 4字节

        // 位置属性
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle, 3, GLES20.GL_FLOAT,
            false, stride, 0
        )

        // 颜色属性
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(
            colorHandle, 4, GLES20.GL_FLOAT,
            false, stride, 3 * 4  // 位置后偏移3个浮点数
        )

        // 绘制三角形
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.size,
            GLES20.GL_UNSIGNED_SHORT,
            0
        )

        // 禁用顶点数组
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)

        // 解绑
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun release() {
        // 清理资源
        GLES20.glDeleteProgram(waveProgram)
        if (waveVerticesBufferId != 0) {
            val buffers = intArrayOf(waveVerticesBufferId, waveIndicesBufferId)
            GLES20.glDeleteBuffers(2, buffers, 0)
        }
    }
}