import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;


public class Streams_1 {
    public static void main(String[] args) {

        System.out.println("-".repeat(30));
        //Problem-1
        //https://www.youtube.com/watch?v=xifdVmHOAWc&list=PL63BDXJjNfTElajNCfg_2u_pbe1Xi7uTy
        // Given a sentence, find the word which have highest length
        // Input : "I am learning Streams in Java"
        // Output : "learning"
        String s1 = "I am learning Streams in Java";
        String ans1 = Arrays.stream(s1.split(" ")).max(Comparator.comparingInt(String::length)).get();
        String ans11 = Arrays.stream(s1.split(" ")).max(Comparator.comparingInt(word->word.length())).get();
        System.out.println(ans1);
        System.out.println(ans11);
        System.out.println("-".repeat(30));


        //Problem-2
        //https://www.youtube.com/watch?v=6iyDi7q11Lk&list=PL63BDXJjNfTElajNCfg_2u_pbe1Xi7uTy&index=2
        //Remove duplicates from a string and return in same order
        //I/P : dabahdahegg
        //O/P : dabheg
        String s2 = "dabahdahegg";
        s2.chars().distinct().mapToObj(ch->(char)ch).forEach(System.out::print);
        System.out.println();
        Arrays.stream(s2.split("")).distinct().forEach(System.out::print);
        System.out.println();
        System.out.println("-".repeat(30));


        //Problem-3
        //https://www.youtube.com/watch?v=lrguO_nZJyg&list=PL63BDXJjNfTElajNCfg_2u_pbe1Xi7uTy&index=3
        //Given a sentence, find the word that 2nd(nth) highest length
        //I/P : I am learning Streams API in java
        //O/P : Streams
        String s3 = "I am learning Streams API in java";
        String ans31 = Arrays.stream(s3.split(" "))
                .sorted(Comparator.comparingInt(String::length).reversed()).skip(1).findFirst().get();
        System.out.println(ans31);
        System.out.println("-".repeat(30));


        //Problem-4
        //https://www.youtube.com/watch?v=qpF-olV37c0&list=PL63BDXJjNfTElajNCfg_2u_pbe1Xi7uTy&index=4
        //Find the 2nd Highest length word in a sentence
        //I/P:  I am learning Streams API in java
        //O/P: Streams

        String s4 = "I am learning Streams API in java";
        int ans41 = Arrays.stream(s4.split(" ")).map(s->s.length())
                .sorted(Comparator.reverseOrder()).skip(1).findFirst().get();
        System.out.println(ans41);
        System.out.println("-".repeat(30));


        //Problem-5
        //https://www.youtube.com/watch?v=cU6nA1cnZ1g&list=PL63BDXJjNfTElajNCfg_2u_pbe1Xi7uTy&index=5
        //Given a sentence, find the occurance of a each string
        //I/P: I am learning Streams API in java java
        //O/P: {java=2, in=1, I=1, API=1, learning=1, am=1, Streams=1}
        String s5 = "I am learning Streams API in java java";
        Map<String,Long> ans51 = Arrays.stream(s5.split(" "))
                .collect(Collectors.groupingBy(x-> x,Collectors.counting()));
        System.out.println(ans51);
        System.out.println("-".repeat(30));

        //Problem-6
        //https://www.youtube.com/watch?v=F1wB6DjspUc&list=PL63BDXJjNfTElajNCfg_2u_pbe1Xi7uTy&index=6
        //Given a sentence, find the words with a specified number of vowels
        //No of Vowels = 2
        //I/P: I am learning Streams API in java
        //O/P:Streams
        //API
        //java
        String s6 = "I am learning Streams API in java";
        Arrays.stream(s6.split(" "))
                .filter(x->x.replaceAll("[^aeiouAEIOU]","").length()==2)
                .forEach(System.out::println);
        System.out.println("-".repeat(30));


    }
}
