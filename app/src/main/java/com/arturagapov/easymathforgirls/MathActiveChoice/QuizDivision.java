package com.arturagapov.easymathforgirls.MathActiveChoice;

/**
 * Created by Artur Agapov on 17.08.2016.
 */
public class QuizDivision {
    private int firstNumber;
    private int secondNumber;
    private int rightAnswer;
    private int wrongtAnswer1;
    private int wrongtAnswer2;
    private int wrongtAnswer3;
    private static int level;

    public void setLevel(int level) {
        this.level = level;
    }

    public void generation() {
        if (level == 1) {
            do {
                do {
                    secondNumber = (int) (Math.random() * 10);
                } while (secondNumber <= 1);
                do {
                    firstNumber = secondNumber * (int) (Math.random() * 10);
                } while (firstNumber <= 2 || firstNumber == secondNumber);
                rightAnswer = firstNumber / secondNumber;
            }
            while (firstNumber % secondNumber != 0);
            do {
                int x = (int) (Math.random() * 2);
                if (x == 1) {
                    if (rightAnswer > 4) {
                        wrongtAnswer1 = rightAnswer - (int) (Math.random() * 3);
                        wrongtAnswer2 = rightAnswer + (int) (Math.random() * 3);
                        wrongtAnswer3 = rightAnswer - 1;
                    } else {
                        wrongtAnswer1 = rightAnswer + (int) (Math.random() * 2);
                        wrongtAnswer2 = rightAnswer + (int) (Math.random() * 3);
                        wrongtAnswer3 = rightAnswer - 1;
                    }
                } else {
                    if (rightAnswer > 4) {
                        wrongtAnswer1 = rightAnswer + (int) (Math.random() * 3);
                        wrongtAnswer2 = rightAnswer - (int) (Math.random() * 3);
                        wrongtAnswer3 = rightAnswer + 1;
                    } else {
                        wrongtAnswer1 = rightAnswer + (int) (Math.random() * 2);
                        wrongtAnswer2 = rightAnswer + (int) (Math.random() * 3);
                        wrongtAnswer3 = rightAnswer - 1;
                    }
                }
            }
            while (wrongtAnswer1 <= 0 || wrongtAnswer2 <= 0 || wrongtAnswer3 <= 0 || wrongtAnswer1 == wrongtAnswer2 || wrongtAnswer1 == wrongtAnswer3 || wrongtAnswer3 == wrongtAnswer2 || wrongtAnswer1 == rightAnswer || wrongtAnswer2 == rightAnswer || wrongtAnswer3 == rightAnswer);
        }
        if (level == 2) {
            do {
                do {
                    secondNumber = (int) (Math.random() * 20);
                } while (secondNumber <= 1);
                do {
                    firstNumber = secondNumber * (int) (Math.random() * 30);
                } while (firstNumber <= 2 || firstNumber == secondNumber);
                rightAnswer = firstNumber / secondNumber;
            }
            while (firstNumber % secondNumber != 0);
            do {
                int x = (int) (Math.random() * 2);
                if (x == 1) {
                    if (rightAnswer > 4) {
                        wrongtAnswer1 = rightAnswer - (int) (Math.random() * 3);
                        wrongtAnswer2 = rightAnswer + (int) (Math.random() * 3);
                        wrongtAnswer3 = rightAnswer - 1;
                    } else {
                        wrongtAnswer1 = rightAnswer + (int) (Math.random() * 2);
                        wrongtAnswer2 = rightAnswer + (int) (Math.random() * 3);
                        wrongtAnswer3 = rightAnswer - 1;
                    }
                } else {
                    if (rightAnswer > 4) {
                        wrongtAnswer1 = rightAnswer + (int) (Math.random() * 3);
                        wrongtAnswer2 = rightAnswer - (int) (Math.random() * 3);
                        wrongtAnswer3 = rightAnswer + 1;
                    } else {
                        wrongtAnswer1 = rightAnswer + (int) (Math.random() * 2);
                        wrongtAnswer2 = rightAnswer + (int) (Math.random() * 3);
                        wrongtAnswer3 = rightAnswer - 1;
                    }
                }
            }
            while (wrongtAnswer1 <= 0 || wrongtAnswer2 <= 0 || wrongtAnswer3 <= 0 || wrongtAnswer1 == wrongtAnswer2 || wrongtAnswer1 == wrongtAnswer3 || wrongtAnswer3 == wrongtAnswer2 || wrongtAnswer1 == rightAnswer || wrongtAnswer2 == rightAnswer || wrongtAnswer3 == rightAnswer);
        }
        if (level == 3) {
            do {
                secondNumber = (int) (Math.random() * 50);
            } while (secondNumber <= 1);
            do {
                firstNumber = secondNumber * (int) (Math.random() * 100);
            } while (firstNumber <= 2 || firstNumber == secondNumber);
            rightAnswer = firstNumber / secondNumber;
        }
        while (firstNumber % secondNumber != 0) ;
        do {
            int x = (int) (Math.random() * 2);
            if (x == 1) {
                if (rightAnswer > 4) {
                    wrongtAnswer1 = rightAnswer - (int) (Math.random() * 3);
                    wrongtAnswer2 = rightAnswer + (int) (Math.random() * 3);
                    wrongtAnswer3 = rightAnswer - 1;
                } else {
                    wrongtAnswer1 = rightAnswer + (int) (Math.random() * 2);
                    wrongtAnswer2 = rightAnswer + (int) (Math.random() * 3);
                    wrongtAnswer3 = rightAnswer - 1;
                }
            } else {
                if (rightAnswer > 4) {
                    wrongtAnswer1 = rightAnswer + (int) (Math.random() * 3);
                    wrongtAnswer2 = rightAnswer - (int) (Math.random() * 3);
                    wrongtAnswer3 = rightAnswer + 1;
                } else {
                    wrongtAnswer1 = rightAnswer + (int) (Math.random() * 2);
                    wrongtAnswer2 = rightAnswer + (int) (Math.random() * 3);
                    wrongtAnswer3 = rightAnswer - 1;
                }
            }
        }
        while (wrongtAnswer1 <= 0 || wrongtAnswer2 <= 0 || wrongtAnswer3 <= 0 || wrongtAnswer1 == wrongtAnswer2 || wrongtAnswer1 == wrongtAnswer3 || wrongtAnswer3 == wrongtAnswer2 || wrongtAnswer1 == rightAnswer || wrongtAnswer2 == rightAnswer || wrongtAnswer3 == rightAnswer);
    }

    public int getFirstNumber() {
        return firstNumber;
    }

    public int getSecondNumber() {
        return secondNumber;
    }

    public int getRightAnswer() {
        return rightAnswer;
    }

    public int getWrongtAnswer1() {
        return wrongtAnswer1;
    }

    public int getWrongtAnswer2() {
        return wrongtAnswer2;
    }

    public int getWrongtAnswer3() {
        return wrongtAnswer3;
    }
}
