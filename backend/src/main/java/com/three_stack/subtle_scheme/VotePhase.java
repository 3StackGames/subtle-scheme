package com.three_stack.subtle_scheme;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.three_stack.digital_compass.backend.*;
import org.omg.CORBA.DynAnyPackage.Invalid;

public class VotePhase extends BasicPhase {
	private QuestionService questionService;

	@Override
	public Class getAction() {
		return VoteAction.class;
	}

	@Override
	public void setup(BasicGameState state) {
		questionService = new QuestionService();
	}

	public BasicGameState processAction(BasicAction action, BasicGameState state) throws InvalidInputException {
		GameState gameState = (GameState) state;
		VoteAction voteAction = (VoteAction) action;

		// add vote
		String votedAnswer = voteAction.getAnswer();
		String believer = voteAction.getPlayer();

		Question currentQuestion = gameState.getCurrentQuestion();
		String answer = currentQuestion.getAnswer();

		if (votedAnswer.equals(answer)) {
			currentQuestion.getBelievers().add(believer);
			gameState.incrementVoteCount();
		} else {
			//loop through all lies to find the match
			for (Lie lie : gameState.getLies()) {
				if (lie.getLie().equals(votedAnswer)) {
					lie.getBelievers().add(believer);
					gameState.incrementVoteCount();
					// stop looking for a match
					break;
				}
			}
		}
		// check if done
		if (gameState.getVoteCount() == gameState.getPlayers().size()) {
			tallyScores(gameState);
			gameState.transitionPhase(new RevealPhase());
		}
		return state;
	}

	private void tallyScores(GameState gameState) {
		final int pointsForCorrect = gameState.getCurrentInstruction().getCorrectAnswerPointValue();
		final int pointsForTrick = gameState.getCurrentInstruction().getTrickBonusPointValue();


		//give correct points
		int correctCount = 0;
		for(String player : gameState.getCurrentQuestion().getBelievers()) {
			givePlayerPoint(gameState, player, pointsForCorrect);
			correctCount++;
		}
		//give lie points
		for(Lie lie : gameState.getLies()) {
			int points = lie.getBelievers().size() * pointsForTrick;
			givePlayerPoint(gameState, lie.getLiar(), points);
		}

		//update metadata
		questionService.updateQuestionMetadata(gameState.getCurrentQuestion().getId(), gameState.getLies().size(), correctCount);
	}
	
	private void givePlayerPoint(GameState gameState, String displayName, int value) {
		for(BasicPlayer player : gameState.getPlayers()) {
			if(player.getDisplayName().equals(displayName)) {
				int newScore = player.getScore() + value;
				player.setScore(newScore);
				//found so we can return
				return;
			}
		}
	}
	
	@Override
	public BasicGameState onDisplayActionComplete(BasicGameState state) {
    	if(state.isDisplayComplete()) {
    		GameState gameState = (GameState) state;
    		Question cq = gameState.getCurrentQuestion();
    		List<Lie> lies = gameState.getLies();
    		
    		Set<BasicPlayer> players = new HashSet<BasicPlayer>(gameState.getPlayers());
    		players.remove(cq.getBelievers());
    		for (Lie lie : lies) {
    			players.remove(lie.getBelievers());
    		}
    		
    		for (BasicPlayer player : players) {
    			VoteAction voteAction = new VoteAction();
    			voteAction.setPlayer(player.getDisplayName());
    			
    			Lie playerLie = null;
    			for (Lie lie : lies) {
    				if (lie.getLiar().equals(player.getDisplayName())) {
    					playerLie = lies.remove(lies.indexOf(lie));
    					break;
    				}
    			}

    			Random r = new Random();
    			int i = r.nextInt(lies.size()+1);
    			
    			if (i == 0) {
    				voteAction.setAnswer(cq.getAnswer());
    			}
    			else {
    				voteAction.setAnswer(lies.get(i-1).getLie());  				
    			}

				try {
					state = processAction(voteAction, state);
				} catch (InvalidInputException e) {
					System.out.println("God help us. Why are we here?");
					e.printStackTrace();
				}
				
				if(playerLie != null)
					lies.add(playerLie);
			}
    	} else {
    		super.onDisplayActionComplete(state);
    	}
    	return state;
    }

}
