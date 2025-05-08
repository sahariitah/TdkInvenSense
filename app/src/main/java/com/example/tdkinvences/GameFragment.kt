package com.example.tdkinvences

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.util.logging.Handler
import kotlin.random.Random

class GameFragment : Fragment() {

    private lateinit var ball: View
    private lateinit var obstacle: View
    private lateinit var gameArea: FrameLayout
    private lateinit var scoreText: TextView
    private lateinit var playButton: Button

    private var score = 0
    private var ballSpeedY = -10 // first speed of ball
    private var isSuperJump = false // super jump
    private var isGameOver = false // game over
    private val handler = android.os.Handler()
    private lateinit var gameLoop: Runnable
    private lateinit var sharedViewModel: SharedViewModel
    private var handDistance = 50

    private val obstacleSpeed = 10f //constant speed of obstacle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        ball = view.findViewById(R.id.ball)
        obstacle = view.findViewById(R.id.obstacle)
        gameArea = view.findViewById(R.id.game_area)
        scoreText = view.findViewById(R.id.score_text)
        playButton = view.findViewById(R.id.play_button)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        resetObstaclePosition()
        resetBallPosition()
        observeDistanceData()

        playButton.setOnClickListener {
            startGame()
        }

        return view
    }

    private fun observeDistanceData() {
        sharedViewModel.distance.observe(viewLifecycleOwner) { distance ->
            Log.d("GameFragment", "Received distance: $distance")

            handDistance = distance.toInt() ?: run {
                Log.e("GameFragment", "Invalid distance format: $distance")
                return@observe
            }
            Log.d("GameFragment", "Updated handDistance: $handDistance")
        }
    }

    private fun startGame() {
        // remove last loop
        handler.removeCallbacksAndMessages(null)

        playButton.visibility = View.GONE
        ball.visibility = View.VISIBLE
        obstacle.visibility = View.VISIBLE
        score = 0
        ballSpeedY = -10
        isGameOver = false
        updateScoreText()

        resetObstaclePosition()
        resetBallPosition()


        gameLoop = Runnable {
            updateGame()
            handler.postDelayed(gameLoop, 30)
        }
        handler.post(gameLoop)
    }

    private fun updateGame() {

        if (isGameOver) return

        moveObstacle()
        handleJump()
        checkCollision()
    }

    private fun moveObstacle() {

        obstacle.x -= obstacleSpeed
        if (obstacle.x + obstacle.width < 0) {
            resetObstaclePosition()
            score++ // The score increases if the ball does not hit the obstacle
            updateScoreText()
        }
    }

    private fun resetObstaclePosition() {

        obstacle.x = gameArea.width.toFloat()
        obstacle.y = (gameArea.height - obstacle.height - (0..300).random()).toFloat()
    }

    private fun resetBallPosition() {
        // The ball always remains in a fixed horizontal position
        ball.x = 200f
        ball.y = (gameArea.height - ball.height).toFloat()
    }

    private fun handleJump() {
        // if the hand is on the sensor
        if (handDistance > 30 && !isSuperJump) {
            isSuperJump = true
            ballSpeedY = -45 // the bull jumps
        }


        ball.y += ballSpeedY
        ballSpeedY += 1

        // managing the jump of the ball
        if (ball.y >= gameArea.height - ball.height) {
            ball.y = (gameArea.height - ball.height).toFloat()
            ballSpeedY = -10 // usual jump
            isSuperJump = false
        } else if (ball.y <= 0) {
            ball.y = 0f
            ballSpeedY = 10
        }
    }

    private fun checkCollision() {
        val ballLeft = ball.x
        val ballTop = ball.y
        val ballRight = ballLeft + ball.width
        val ballBottom = ballTop + ball.height

        val obstacleLeft = obstacle.x
        val obstacleTop = obstacle.y
        val obstacleRight = obstacleLeft + obstacle.width
        val obstacleBottom = obstacleTop + obstacle.height

        if (ballRight > obstacleLeft && ballLeft < obstacleRight && ballBottom > obstacleTop && ballTop < obstacleBottom) {
            // if the ball hits the obstacle
            isGameOver = true
            handler.removeCallbacks(gameLoop)
            score = 0
            scoreText.text = "Game Over!"
            ball.visibility = View.GONE
            obstacle.visibility = View.GONE
            playButton.visibility = View.VISIBLE
        }
    }

    private fun updateScoreText() {
        scoreText.text = "Score: $score"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(gameLoop)
    }
}










