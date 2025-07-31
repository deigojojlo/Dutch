package main.java.game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import main.java.util.Pair;

/**
 * Represents an AI-controlled player.
 * The AI plays with a certain difficulty level and memorizes some cards with a
 * chance of forgetting them.
 * It can also announce the end of the game based on specific criteria.
 */
public class PlayerComputerModel extends PlayerModel {
    /**
     * Difficulty level of the computer player.
     * 0 for easy, 1 for hard.
     */
    private int difficulty;

    /**
     * Random number generator for the computer player's decisions.
     */
    private final Random random;

    /**
     * Current number of cards memorized by the computer.
     */
    private int memorySize;

    /**
     * Probability that the computer forgets a memorized card during each turn.
     * Initially set to 0.1 and increases gradually.
     */
    private double forgetProbability;

    /**
     * Memory structure of the computer.
     * Each entry corresponds to a player, and maps card positions to known cards.
     */
    private ArrayList<HashMap<Integer, CardModel>> memory;

    /**
     * Total number of players in the game.
     * Shared by all computer players (static).
     */
    private static int nbOfPlayer;

    /**
     * Constructs a computer-controlled player with specified game ID and difficulty level.
     * Initializes the memory system, random number generator, readiness status, and forget probability.
     * For each player in the game, creates an empty memory map.
     *
     * @param gameId     The unique ID of the player.
     * @param difficulty The difficulty level of the computer (0 for easy, 1 for hard).
     */
    public PlayerComputerModel(int gameId, int difficulty) {
        super(gameId);
        memorySize = 0;
        this.difficulty = difficulty;
        this.random = new Random();
        this.isReady = true;
        this.forgetProbability = 0.1;
        this.memory = new ArrayList<>();
        for (int i = 0; i < nbOfPlayer; i++) {
            memory.add(new HashMap<>());
        }
    }


     // -----------------------------
    // Getters and Setters
    // -----------------------------

    /**
     * Gets the difficulty level of the computer player.
     *
     * @return The difficulty level.
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the difficulty level of the computer player.
     *
     * @param difficulty The new difficulty level.
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Gets the memory of the computer player.
     *
     * @return A list of hashmaps representing memorized cards.
     */
    public ArrayList <HashMap<Integer, CardModel>> getMemory(){
        return memory;
    }

    /**
     * Gets the random number generator used by the computer player.
     *
     * @return The random number generator.
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Gets the number of cards currently memorized by the computer.
     *
     * @return The memory size.
     */
    public int getMemorySize(){
        return memorySize;
    }

    /**
     * Gets the number of players in the game.
     *
     * @return Number of players.
     */
    public static int getNbOfPlayer(){
        return nbOfPlayer;
    }

    /**
     * Sets the number of players in the game (static).
     *
     * @param nb Number of players.
     */
    public static void setNbofPlayer(int nb){
        nbOfPlayer = nb;
    }


    // -----------------------------
    // Memory Management
    // -----------------------------

     /**
     * Memorizes a card at the given position in the computer's memory.
     *
     * @param position Position to memorize the card at.
     * @param card     Card to memorize.
     */
    public void memorizeCard(int position, CardModel card) {
        memory.get(nbOfPlayer - 1).put(position, card);
        if (memorySize < 4) {
            memorySize++;
        }
    }

    /**
     * Memorizes a card of another player at a given position.
     *
     * @param Player   The player index.
     * @param position The card position.
     * @param card     The card to memorize.
     */
    public void memorizeCardOfPlayer(int Player,int position, CardModel card){
        memory.get(Player).put(position,card);
    }

    /**
     * Replaces a memorized card at a position with a new card in the computer's memory.
     *
     * @param position The position of the card to replace.
     * @param c        The new card.
     */
    public void switchMemory(int position, CardModel c) {
        memory.get(nbOfPlayer - 1).put(position, c);                     
    }

    /**
     * Forgets a memorized card with a certain probability.
     */
    public void forgetCards() {
        if (random.nextDouble() < forgetProbability) {
            memory.get(nbOfPlayer - 1).remove(memorySize);
            memorySize = memorySize == 0 ? 0 : memorySize -- ;
        }
    }

    /**
     * Increments the memory size by 1.
     */
    public void incMemorySize(){
        memorySize ++;
    }

    /**
     * Increases the forget probability by 0.05, capped at 1.0.
     */
    public void add_forgetProbability() {
        if (forgetProbability == 1.0) {
            forgetProbability = 0.1;
        }
        forgetProbability += 0.05;
    }

    /**
     * Swaps cards between the memory of two players.
     *
     * @param player1   First player index.
     * @param position1 Position in first player's memory.
     * @param player2   Second player index.
     * @param position2 Position in second player's memory.
     */
    public void swapCardMemory(int player1,int position1,int player2,int position2){
        CardModel temp = memory.get(player1).get(position1);
        memory.get(player1).put(position2, memory.get(player2).get(position2));
        memory.get(player1).put(position1, temp);
    }

    /**
     * Calculates the sum of points in the computer's memory.
     *
     * @return Total score of memorized cards.
     */
    public int getScoreInMemory() {
            int sum = 0;
        for (CardModel card : memory.get(nbOfPlayer - 1).values()) {
            sum += card.getPoint();
        }
        return sum;
    }

    /**
     * Clears the AI's memory by emptying all stored card information for every player.
     */
    public void clearMemory() {
        for (HashMap<Integer, CardModel> playerMemory : memory) {
            playerMemory.clear();
        }
        memorySize = 0;
}


    // -----------------------------
    // Game Logic - Turns
    // -----------------------------

    /**
     * Plays a turn (draw) for an easy computer player.
     *
     * @param hand The drawn card.
     * @return An action array representing the move.
     */
    public int[] playComputerTurn1(CardModel hand) {
        int[] action = new int[7];
        boolean willSwap = random.nextBoolean();
        if (willSwap) {
            // Échanger avec une carte au hasard dans son deck
            int randomPosition = random.nextInt(4);
            action[0] = 1; // switch
            action[1] = randomPosition;
            System.out.println(
                    "L'ia veut echanger la carte de sa main avec sa carte à la postion : " + randomPosition);
            return action;
        } else {
            if (hand.hasPower()) {
                return choosePower(hand, action);
            }
        }

        action[0] = -1;
        System.out.println("L'ia veut défausser la carte dans sa main");
        return action;
    }

    /**
     * Plays a turn (draw) for a hard computer player.
     *
     * @param hand The drawn card.
     * @return An action array representing the move.
     */
    public int[] playComputerTurn2(CardModel hand) {
        int[] action = new int[7];
        if (memory.get(nbOfPlayer - 1).isEmpty()) {
            if (!hand.hasPower() || hand.getPoint() == 0) {
                action[0] = 1;
                action[1] = memorySize;
                System.out.println("0.L'ia veut echanger la carte de sa main avec sa carte à la postion : " + action[1]);
                memorizeCard(memorySize, new CardModel(hand.getColor(), hand.getValue()));
            } else {
                choosePower(hand, action);
            }
        } else {
            int position = switchMax(hand);
            if (position != -1) {
                System.out.println(
                        "1.L'ia veut echanger la carte de sa main avec sa carte à la postion : " + position);
                action[0] = 1;
                action[1] = position;
                switchMemory(position, new CardModel(hand.getColor(), hand.getValue()));
            } else {
                System.out.println("Pouvoir? : " + hand.hasPower());
                if (hand.hasPower()) {
                    return choosePower(hand, action);
                }
                else if (hand.getPoint() < 5 && memorySize != 4) {
                    position = memorySize;
                    System.out.println("2.L'ia veut échanger sa main avec la carte à la position : " + position);
                    action[0] = 1;
                    action[1] = position;
                    memorizeCard(position, new CardModel(hand.getColor(), hand.getValue()));
                } else {
                    System.out.println("L'ia veut défausser la carte dans sa mainjnj");
                    action[0] = -1;
                }
            }
        }
        add_forgetProbability();
        forgetCards();
        return action;
    }

    /**
     * Determines whether to take the discard pile card for an easy computer.
     *
     * @return A position index or -1 if not.
     */
    public int playComputerTurn1_WantDiscard() {
        int action = -1;
        if (random.nextBoolean()) {
            action = random.nextInt(4);
        }
        return action;
    }

    /**
     * Determines whether to take the discard pile card for a hard computer.
     *
     * @param topDiscard The top card of the discard pile.
     * @return A position index or -1 if not.
     */
    public int playComputerTurn2_WantDiscard(CardModel topDiscard) {
        int action = -1;
        if (topDiscard != null) {
            if (memory.get(nbOfPlayer - 1).isEmpty()) {
            if (topDiscard.getPoint() < 6) {
                memorizeCard(0, new CardModel(topDiscard.getColor(),topDiscard.getValue()));
                System.out.println("L'ia veut echanger la carte de la défausse avec sa carte à la postion : " + 0);
                action = 0;
                add_forgetProbability();
                forgetCards();
            } else {
                return action;
            } 
        } else {
            int position = switchMax(topDiscard);
            if (topDiscard.getPoint() < 4) {
                if(memorySize < 4){
                    System.out.println("0.L'ia veut echanger la carte de la défausse avec sa carte à la postion : " + memorySize);
                    action = memorySize;
                    memorizeCard(memorySize, new CardModel(topDiscard.getColor(),topDiscard.getValue()));
                    add_forgetProbability();
                    forgetCards();
                } else {
                    System.out.println("1.L'ia veut echanger la carte de la défausse avec sa carte à la postion : " + maxdeck());
                    action = maxdeck();
                    memorizeCard(action, new CardModel(topDiscard.getColor(),topDiscard.getValue()));
                    add_forgetProbability();
                    forgetCards();
                }
            }
            else if (position != -1 && topDiscard.getPoint() < 8) {
                switchMemory(position, new CardModel(topDiscard.getColor(),topDiscard.getValue()));
                System.out.println("2.L'ia veut echanger la carte de la défausse avec sa carte à la postion : " + position);
                action = position;
                add_forgetProbability();
                forgetCards();
                }
            }
        } 
        return action;
    }

    /**
     * Determines whether to take the discard pile card depending on difficulty.
     *
     * @param topDiscard The top card of the discard pile.
     * @return Position index or -1.
     */
    public int playComputerTurn_WantDiscard(CardModel topDiscard) {
        return switch (difficulty) {
            case 0 -> playComputerTurn1_WantDiscard();
            case 1 -> playComputerTurn2_WantDiscard(topDiscard);
            default -> 5;
        };
    }

    /**
     * Main method for playing the turn depending on difficulty.
     *
     * @param hand The card drawn.
     * @return The chosen action.
     */
    public int[] playComputerTurn_Pick(CardModel hand) {
        return switch (difficulty) {
            case 0 -> playComputerTurn1(hand);
            case 1 -> playComputerTurn2(hand);
            default -> null;
        };
    }
    // -----------------------------
    // Game Logic - Powers & Decisions
    // -----------------------------

    /**
     * Chooses and executes a power if the card has one.
     *
     * @param c      The power card.
     * @param action The action array to populate.
     * @return Updated action array.
     */
    public int[] choosePower(CardModel c, int[] action) {
        if (c.hasPower()) {
            System.out.println(c.getValue());
            switch (c.getValue()) {
                case CardModel.CardValue.SEVEN, CardModel.CardValue.EIGHT -> {
                    if (difficulty == 1) {
                        if(memorySize < 4){
                            System.out.println("L'ia regarde sa carte à la position " + (memorySize));
                            action[0] = 2;
                            action[1] = memorySize;
                        }else{
                            action[0] = -1;
                        }
                    }else{
                        action[0] = 2;
                        action[1] = random.nextInt(4);
                    }
                }
                case CardModel.CardValue.NINE, CardModel.CardValue.TEN -> {
                    if (difficulty == 1) {
                        if (memorySize < 4) {
                            action[0] = 3;
                            action[1] = nbOfPlayer - 1;
                            action[2] = memorySize;
                            System.out.println("L'ia veut regarder la carte " + memorySize + " de son deck ");
                            incMemorySize();
                        } else{
                            int joueur = findPlayer(); 
                            int position = memory.get(joueur).size() < 4 ? memory.get(joueur).size() : memory.get(joueur).size() - 1 ;
                            System.out.println("L'ia veut regarder la carte " + position + " du joueur " + joueur);
                            action[0] = 3;
                            action[1] = joueur;
                            action[2] = position;
                        }
                    } else {
                        action[0] = 3;
                        action[1] = random.nextInt(nbOfPlayer);
                        action[2] = random.nextInt(4);
                    }
                }
                case CardModel.CardValue.JACK, CardModel.CardValue.QUEEN -> {
                    if (difficulty == 1) {
                        Pair<Integer,Integer> min = minCardOfPlayer();
                        if (memorySize != 0 && !memory.get(min.getKey()).isEmpty() && memory.get(min.getKey()).get(min.getValue()).getPoint() < memory.get(nbOfPlayer - 1).get(maxdeck()).getPoint()) {
                            int joueur1 = min.getKey();
                            int joueur2 = (joueur1 + nbOfPlayer + 1) % nbOfPlayer;
                            int position1 = min.getValue();
                            int position2 = maxdeck();
                            System.out.println("L'ia veut échanger sa carte " + position1 
                                + " avec la carte " + position2 + " du joueur " + joueur2); 
                            action[0] = 4;
                            action[1] = joueur1;
                            action[2] = position1;
                            action[3] = joueur2;
                            action[4] = position2;
                        } else {
                            int joueur1 =  random.nextInt(nbOfPlayer - 1) ;
                            int position1 = random.nextInt(4);
                            int joueur2 = random.nextInt(nbOfPlayer - 1);
                            int position2 = random.nextInt(4);
                            System.out.println("L'ia veut échanger la carte " + position1 + " du joueur " + joueur1
                                + " avec la carte " + position2 + " du joueur " + joueur2); 
                            action[0] = 4;
                            action[1] = joueur1;
                            action[2] = position1;
                            action[3] = joueur2;
                            action[4] = position2;
                        } 

                    } else {
                        action[0] = 4;
                        action[1] = random.nextInt(nbOfPlayer);
                        action[2] = random.nextInt(4);
                        action[3] = random.nextInt(nbOfPlayer);
                        action[4] = random.nextInt(4);
                    }
                    
                }
                case CardModel.CardValue.KING -> {
                    if (difficulty == 1) {
                        Pair<Integer,Integer> min = minCardOfPlayer();
                        if (memorySize < 4 && memory.get(min.getValue()) != null) {
                            action[0] = 5;
                            action[1] = nbOfPlayer - 1;
                            action[2] = memorySize;
                            action[3] = min.getKey();
                            action[4] = min.getValue();
                            action[5]= 0;
                        }
                        else if (memory.get(min.getValue()) != null) {
                            action[0] = 5;
                            action[1] = nbOfPlayer - 1;
                            action[2] = maxdeck();
                            action[3] = min.getKey();
                            action[4] = min.getValue();
                            action[5]= 1;
                        } else {
                            int joueur1 =  random.nextInt(nbOfPlayer - 1);
                            int position1 = random.nextInt(4);
                            int joueur2 = random.nextInt(nbOfPlayer - 1);
                            int position2 = random.nextInt(4);
                            System.out.println("L'ia veut échanger la carte " + position1 + " du joueur " + joueur1 + " avec la carte " + position2 + " du joueur " + joueur2); 
                            action[0] = 4;
                            action[1] = joueur1;
                            action[2] = position1;
                            action[3] = joueur2;
                            action[4] = position2;
                        }
                    } else {
                        int joueur1 =  random.nextInt(nbOfPlayer - 1);
                        int position1 = random.nextInt(4);
                        int joueur2 = random.nextInt(nbOfPlayer - 1);
                        int position2 = random.nextInt(4);
                        System.out.println("L'ia veut échanger la carte " + position1 + " du joueur " + joueur1 + " avec la carte " + position2 + " du joueur " + joueur2); 
                        action[0] = 4;
                        action[1] = joueur1;
                        action[2] = position1;
                        action[3] = joueur2;
                        action[4] = position2;
                    }
                }
                default -> throw new IllegalStateException("Unexpected card value: " + hand.getValue());
            }
        }

        return action;
    }

    /**
     * Determines whether the king effect should be activated.
     *
     * @param c1     The king card.
     * @param action The action array.
     * @return True if the effect should be triggered.
     */
    public boolean activeKingEffect(CardModel c1, int[] action){
        if (action[5] == 0 && memory.get(action[3]).get(action[4]) != null) {
            return c1.getPoint() > 5 && c1.getPoint() > memory.get(action[3]).get(action[4]).getPoint(); 
            } 
        else if(action[5] == 1 && memory.get(action[3]) != null){
            return c1.getPoint() > memory.get(action[3]).get(action[4]).getPoint();
        }
        else {
            return random.nextBoolean();
        }
    }

    /**
     * Returns the position of the highest point card in the computer's memory.
     *
     * @return The index of the max card.
     */
    public int maxdeck(){
        int max = 0;
        for (int i = 0; i < memory.get(nbOfPlayer - 1).size(); i++) {
            if (memory.get(nbOfPlayer - 1).get(i).getPoint() > memory.get(nbOfPlayer - 1).get(max).getPoint()) {
                max = i;
            }
        }
        return max;
    }

    /**
     * Returns the player and position of the minimum card seen by the computer.
     *
     * @return A pair (player index, position).
     */
    public Pair<Integer,Integer> minCardOfPlayer(){
        if (memory.get(0).get(0) == null) {
            return new Pair<>(0, 0);
        }
        int minPlayer = 0; 
        int minPosition = 0;
        for (int i = 0; i < memory.size() - 1; i++) {
            for (int j = 0; j < memory.get(i).size(); j++) {
                if (memory.get(i).get(j).getPoint() < memory.get(minPlayer).get(minPosition).getPoint()) {
                    minPlayer = i;
                    minPosition = j;
                }
            }
        } 
        return new Pair<>(minPlayer,minPosition);
    }

    /**
     * Returns the position to swap if a better card is in hand than in memory.
     *
     * @param c The card.
     * @return Position index or -1 if not worthwhile.
     */
    public int switchMax(CardModel c) {
        Integer position = maxdeck();
        CardModel max = memory.get(nbOfPlayer - 1).get(position);
        if (max.getPoint() > c.getPoint()) {
            return position;
        }
        return -1;
    }

    /**
     * Selects a player to observe or act upon based on memory size.
     *
     * @return Index of selected player.
     */
    public int findPlayer(){
        for (int i = 0; i < memory.size() - 2; i++) {
            if (memory.get(i).size() > memory.get(i + 1).size()) {
                return i;
            }
        }
        for (int i = 0; i < memory.size()-1; i++) {
            if (memory.get(i).size() < 4) {
                return i;
            }
        }
        return 0;
    }
    /**
     * Decides whether the computer wants to end the game.
     *
     * @return True if conditions are met to finish.
     */
    public boolean finish(){
        if (difficulty == 1) {
            int n = random.nextInt(3) + 1;
            int score = getScoreInMemory();
            if(memorySize == 3 && score < 6 && n == 3){
                return true;
            }
            else if(memorySize == 4 && score < 10){
                return true;
            }
            return false;
        } else {
            return random.nextBoolean() && random.nextBoolean() && random.nextBoolean() && random.nextBoolean();
        }
    }
}
