
import java.util.*;

public class Help {

    
    public static final String RESET = "\u001B[0m";
    public static final String YELLOW = "\u001B[33m";

    
    public static boolean[] fiftyFiftyMask(Question q) {
        boolean[] show = new boolean[]{true, true, true, true};
        int correctIndex = q.getCorrect() - 'A';
        List<Integer> wrongs = new ArrayList<>();
        for (int i = 0; i < 4; i++) if (i != correctIndex) wrongs.add(i);
        Collections.shuffle(wrongs);
       
        show[wrongs.get(0)] = false;
        show[wrongs.get(1)] = false;
        return show;
    }

   
    public static int[] audiencePoll(Question q) {
        Random rand = new Random();
        int correctIndex = q.getCorrect() - 'A';
        int[] percent = new int[4];
        percent[correctIndex] = 40 + rand.nextInt(31); // 40..70
        int remain = 100 - percent[correctIndex];
        List<Integer> wrongs = new ArrayList<>();
        for (int i = 0; i < 4; i++) if (i != correctIndex) wrongs.add(i);
      
        for (int i = 0; i < wrongs.size() - 1; i++) {
            int v = rand.nextInt(remain + 1);
            percent[wrongs.get(i)] = v;
            remain -= v;
        }
        percent[wrongs.get(2)] = remain;
        return percent;
    }

  
    public static char callFriendSuggest(Question q) {
        Random rand = new Random();
        boolean correct = rand.nextInt(100) < 75; 
        if (correct) return q.getCorrect();
        
        int correctIndex = q.getCorrect() - 'A';
        List<Integer> wrongs = new ArrayList<>();
        for (int i = 0; i < 4; i++) if (i != correctIndex) wrongs.add(i);
        Collections.shuffle(wrongs);
        return (char) ('A' + wrongs.get(0));
    }


    public static void printAudienceResult(int[] percent) {
        System.out.println(YELLOW + "\n📊 Kết quả bình chọn của khán giả:" + RESET);
        char ch = 'A';
        for (int p : percent) {
            System.out.printf("%c: %d%%  ", ch, p);
            ch++;
        }
        System.out.println();
    }

    public static void printCallResult(char suggest, String who) {
        System.out.println(YELLOW + "\n📞 " + who + " nghĩ đáp án là: " + suggest + RESET);
    }

    
    // public static final String RESET = "\u001B[0m";
}
