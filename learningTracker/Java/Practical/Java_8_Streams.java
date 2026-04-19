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


        //Problem-7
        //https://www.youtube.com/watch?v=QERuQaQT74U&list=PL63BDXJjNfTElajNCfg_2u_pbe1Xi7uTy&index=8
        //Divide given integer list into lists of even and odd numbers
        //I/P: {1,2,3,4,5,6,7,8,9,10}
        int arr7[] = {1,2,3,4,5,6,7,8,9,10};
        List<Integer> list7 = Arrays.stream(arr7).boxed().collect(Collectors.toList());
        List<List<Integer>> ans7 = list7.stream()
                                        .collect(Collectors.partitioningBy(x->x%2==0,Collectors.toList()))
                                         .entrySet().stream()
                                          .map(x->x.getValue()).collect(Collectors.toList());
        System.out.println(ans7);
        System.out.println("-".repeat(30));



        //Problem-8
        //https://www.youtube.com/watch?v=y3h47AqCMIc&list=PL63BDXJjNfTElajNCfg_2u_pbe1Xi7uTy&index=8
        //Given a word, find the occurrence of each character
        String s8 = "Mississippi";
        Map<String,Long> ans8 = Arrays.stream(s8.split("")).collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        //Map<String,Long> ans8 = Arrays.stream(s8.split("")).collect(Collectors.groupingBy(x->x,Collectors.counting()));
        System.out.println(ans8);
        System.out.println("-".repeat(30));


        //Problem-9
        //https://www.youtube.com/watch?v=FNvZgvyWqZY&list=PL63BDXJjNfTElajNCfg_2u_pbe1Xi7uTy&index=9
        //Arrange the numbers in Descending/Ascending Order
        int[] arr9 = {1111,2,3,4,5};
        Arrays.stream(arr9).mapToObj(x->x).sorted().collect(Collectors.toList()).forEach(x->System.out.print(x+" "));
        System.out.println();
        Arrays.stream(arr9).mapToObj(x->x).sorted(Comparator.reverseOrder()).collect(Collectors.toList()).forEach(x->System.out.print(x+" "));
        System.out.println();
        System.out.println("-".repeat(30));


        //Problem-10
        //https://www.youtube.com/watch?v=Cprnsyy7v1k&list=PL63BDXJjNfTElajNCfg_2u_pbe1Xi7uTy&index=10
        //Given an array, find the sum of unique elements
        int[] arr10 = { 1,6,7,8,8,9,9,11,11,23,123};
        int ans10 = Arrays.stream(arr10).distinct().sum();
        System.out.println(ans10);
        System.out.println("-".repeat(30));

        }
}

