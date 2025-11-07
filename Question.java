// Question.java
public class Question {
    private String question;
    private String[] options; // length 4, options[0] -> A, options[1] -> B, ...
    private char correct; // 'A','B','C','D'

    public Question(String question, String a, String b, String c, String d, char correct) {
        this.question = question;
        this.options = new String[]{a, b, c, d};
        this.correct = Character.toUpperCase(correct);
    }

    public String getQuestion() { return question; }
    public String[] getOptions() { return options; } // do not modify original if you want to preserve
    public char getCorrect() { return correct; }
}
