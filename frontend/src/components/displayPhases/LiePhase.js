import React, { Component, PropTypes } from 'react'
import ReactDOM, { render } from 'react-dom'
import cx from 'classname'
import autobind from 'autobind-decorator'

export default class LiePhase extends Component {
  constructor(props) {
    super(props)

    this.state = {}
  }

  componentDidMount() {
    const { gameState, engine } = this.props
    const { gameCode } = gameState

    if (gameState.questionCount > 1) {
      engine.displayActionComplete({gameCode })
      return
    }

    setTimeout(() => {
      engine.displayActionComplete({ gameCode })
    }, 1000)
    
    localStorage.setItem('display.gameCode', gameCode);
    localStorage.setItem('display.timestamp', +new Date);
  }

  render() {
    const { gameState } = this.props


    return (
      <div>
        <div className="showGameCode">Game Code: <span>{ gameState.gameCode }</span></div>
        {this.displayQuestion}
      </div>
    )
  }

  get displayQuestion() {
    const { gameState } = this.props
    const {
      displayComplete,
      questionCount,
      players,
      lies,
      currentQuestion,
      currentInstruction: instruction,
    } = gameState

    if (questionCount < 1) {
      return (
        <Instructions
          bonusValue={instruction.trickBonusPointValue}
          correctValue={instruction.correctAnswerPointValue} />
      )
    }

    return (
      <div>
        <div className="lyingTime">
          <h3 className="title">{currentQuestion.question}</h3>
          <WaitingPlayerLies players={players} lies={lies}/>
        </div>
      </div>
    )

  }
}

const WaitingPlayerLies = ({ players, lies }) => {
  let finishedUsers = 0
  const listPlayers = players.map(player => {
    const submittedLie = lies.find(lie => {
      return lie.liar === player.displayName
    })

    if (submittedLie) {
      finishedUsers++
    }

    return (
      <div key={player.displayName} className={cx({
        active: submittedLie
      })}>
        <div className="playerLobbyItem"></div>
      </div>
    )
  })

  const finishElement = (finishedUsers === players.length)
    ? <div className="finished">All lies have been entered!</div>
    : ''

  return (
    <div className="relative">
      {finishElement}
      <div className="WaitingPlayerLies playerColor">
        {listPlayers}
      </div>
    </div>
  )
}

const Instructions = ({ bonusValue, correctValue }) => (
  <div>
    <div className="text-center to-fool">{bonusValue} for everyone you fool</div>
    <div className="text-center for-truth">{correctValue} for finding the truth</div>
  </div>
)