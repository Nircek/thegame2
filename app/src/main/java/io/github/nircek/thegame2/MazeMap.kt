package io.github.nircek.thegame2

import android.graphics.Path
import kotlin.math.*
import kotlin.random.Random

class MazeMap(
    private val X: Int,
    private val Y: Int,
    rnd: Random
) {
    private val xWalls = Array(Y + 1) { BooleanArray(X) { true } }
    private val yWalls = Array(Y) { BooleanArray(X + 1) { true } }
    private var x = (0..X).random(rnd) + .5f
    private var y = (0..Y).random(rnd) + .5f

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

    fun drawOn(path: Path, side: Float, margin: Float, radius: Float): Path {
        val u = (side - 2 * margin) / radius / 2
        path.addCircle(side / 2, side / 2, radius * u, Path.Direction.CCW)

        val jb = max((y - radius).toInt(), 0)
        val je = min((y + radius).toInt(), Y)
        val ib = max((x - radius).toInt(), 0)
        val ie = min((x + radius).toInt(), X)
        for (j in (jb..je)) {
            for (i in (ib..ie)) {
                val dj = (j - y) * u + (side - 2 * margin) / 2 + margin
                val di = (i - x) * u + (side - 2 * margin) / 2 + margin
                if (i < X && xWalls[j][i]) {
                    path.moveTo(di, dj)
                    path.lineTo(di + u, dj)
                }
                if (j < Y && yWalls[j][i]) {
                    path.moveTo(di, dj)
                    path.lineTo(di, dj + u)
                }
            }
        }
        return path
    }

    fun push(direction: Float, dist: Float) {
        x -= (dist * sin(direction * PI / 180f)).toFloat()
        y -= (dist * cos(direction * PI / 180f)).toFloat()
    }
}
