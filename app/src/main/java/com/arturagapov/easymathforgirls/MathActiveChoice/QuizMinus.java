package com.arturagapov.easymathforgirls.MathActiveChoice;

/**
 * Created by Artur Agapov on 15.08.2016.
 */
public class QuizMinus {
    private int firstNumber;
    private int secondNumber;
    private int rightAnswer;
    private int wrongtAnswer1;
    private int wrongtAnswer2;
    private int wrongtAnswer3;
    private int level;

    public void setLevel(int level) {
        this.level = level;
    }

    public void generation() {
        if (level == 1) {
            do {
                firstNumber = (int) (Math.random() * 10);
                secondNumber = (int) (Math.random() * 10);
                rightAnswer = firstNumber - secondNumber;
            } while (firstNumber <= secondNumber || rightAnswer < 4 || secondNumber==0);
            do {
                wrongtAnswer1 = rightAnswer - (int) (Math.random() * 3);
                wrongtAnswer2 = rightAnswer + (int) (Math.random() * 3);
                wrongtAnswer3 = rightAnswer - (int) (Math.random() * 4);
            }
            while (wrongtAnswer1 < 0 || wrongtAnswer3 < 0 || wrongtAnswer1 == wrongtAnswer2 || wrongtAnswer1 == wrongtAnswer3 || wrongtAnswer3 == wrongtAnswer2 || wrongtAnswer1 == rightAnswer || wrongtAnswer2 == rightAnswer || wrongtAnswer3 == rightAnswer);
        }
        if (level == 2) {
            do {
                firstNumber = (int) (Math.random() * 100);
                secondNumber = (int) (Math.random() * 100);
                rightAnswer = firstNumber - secondNumber;
            } while (firstNumber <= secondNumber || rightAnswer < 11 || secondNumber <= 2);
            do {
                if (rightAnswer > 20) {
                    int x = (int) (Math.random() * 2);
                    if (x < 1) {
                        wrongtAnswer1 = rightAnswer - 10;
                    } else {
                        wrongtAnswer1 = rightAnswer + 10;
                    }
                } else {
                    wrongtAnswer1 = rightAnswer - (int) (Math.random() * 3);
                }
                wrongtAnswer2 = rightAnswer + (int) (Math.random() * 3);
                wrongtAnswer3 = rightAnswer - (int) (Math.random() * 4);
            }
            while (wrongtAnswer1 < 0 || wrongtAnswer3 < 0 || wrongtAnswer1 == wrongtAnswer2 || wrongtAnswer1 == wrongtAnswer3 || wrongtAnswer3 == wrongtAnswer2 || wrongtAnswer1 == rightAnswer || wrongtAnswer2 == rightAnswer || wrongtAnswer3 == rightAnswer);
        }
        if (level == 3) {
            do {
                firstNumber = (int) (Math.random() * 1000);
                secondNumber = (int) (Math.random() * 1000);
                rightAnswer = firstNumber - secondNumber;
            } while (firstNumber <= secondNumber || rightAnswer < 101 || secondNumber <= 2);
            do {
                int x = (int) (Math.random() * 2);
                if (x < 1) {
                    wrongtAnswer1 = rightAnswer - 10;
                    wrongtAnswer2 = rightAnswer + 20;
                } else {
                    wrongtAnswer1 = rightAnswer + 10;
                    wrongtAnswer2 = rightAnswer - 20;
                }
                wrongtAnswer3 = rightAnswer + (int) (Math.random() * 4);
            }
            while (wrongtAnswer1 < 0 || wrongtAnswer2 < 0 || wrongtAnswer1 == wrongtAnswer2 || wrongtAnswer1 == wrongtAnswer3 || wrongtAnswer3 == wrongtAnswer2 || wrongtAnswer1 == rightAnswer || wrongtAnswer2 == rightAnswer || wrongtAnswer3 == rightAnswer);
        }
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
