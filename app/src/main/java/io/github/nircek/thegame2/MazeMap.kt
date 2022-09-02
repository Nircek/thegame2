package io.github.nircek.thegame2

import android.graphics.Path
import kotlin.random.Random

class MazeMap(
    val X: Int,
    val Y: Int,
    rnd: Random = Random(0)
) {
    private val xWalls = Array(Y + 1) { BooleanArray(X) { true } }
    private val yWalls = Array(Y) { BooleanArray(X + 1) { true } }
    private var x = (0..X).random(rnd)
    private var y = (0..Y).random(rnd)

    init {
        // init ported from https://gist.github.com/Marwyk2003/d370123aa1551630d6f8df083dfc236c
        val visited = Array(Y) { BooleanArray(X) }
        val neighbours = arrayOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)

        data class StackElement(val xo: Int, val yo: Int, val x: Int, val y: Int)

        val stack = ArrayDeque(listOf(StackElement(-1, -1, 0, 0)))
        while (stack.size > 0) {
            val (xo, yo, x, y) = stack.last()
            stack.removeLast()
            if (visited[y][x])
                continue
            visited[y][x] = true
            if (x == xo)
                xWalls[(y + yo + 1) / 2][xo] = false
            else if (y == yo)
                yWalls[yo][(x + xo + 1) / 2] = false
            neighbours.shuffle(rnd)
            for ((dx, dy) in neighbours) {
                val xn = x + dx
                val yn = y + dy
                if (xn < 0 || xn >= X || yn < 0 || yn >= Y)
                    continue
                stack.addLast(StackElement(x, y, xn, yn))
            }
        }
    }

    fun draw(W: Float): Path {
        val path = Path()

        @Suppress("NAME_SHADOWING")
        fun Array<BooleanArray>.iterateOverLines(func: (i: Int, j: Int) -> Unit) {
            for ((i, e) in this.withIndex()) {
                val i = i + 1
                for ((j, e) in e.withIndex()) {
                    if (!e) continue
                    val j = j + 1
                    func(i, j)
                }
            }
        }

        xWalls.iterateOverLines { i, j ->
            path.moveTo(j * W, i * W)
            path.lineTo((j + 1) * W, i * W)
        }
        yWalls.iterateOverLines { i, j ->
            path.moveTo(j * W, i * W)
            path.lineTo(j * W, (i + 1) * W)
        }
        return path
    }
}
