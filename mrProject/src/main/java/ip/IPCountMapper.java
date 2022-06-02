package ip;


import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 맵 역할을 수행하기 위해서는 Mapper 자바 파일을 상속받아야 함
 * Mapper 파일의 앞의 2개 데이터 타입(LongWritabl, Text)은 분석할 파일의 키과 값의 데이터 타입
 * Mapper 파일의 뒤의 2개 데이터 타입(Text, IntWritable)은 리듀스에 보낼 키와 값의 데이터 타입
 */
public class IPCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    /**
     * 부모 Mapper 자바 파일에 작성된 map 함수를 덮어쓰기 수행
     * map 함수는 분석할 파일의 레코드 1줄마다 실행됨
     * 파일의 라인수가 100개라면, map함수는 100번 실행됨
     */
    @Override
    public void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {

        // 분석할 파일의 한 줄 값
        String line = value.toString();

        String ip = ""; // 로그로부터 추출된 IP
        int forCnt = 0; // 반복 횟수

        // 단어 빈도수 구현은 단어가 아닌 것을 기준으로 단어를 구분함
        // 분석할 한 줄 내용을 단어가 아닌 것으로 나눔
        // word 변수는 단어가 저장됨
        for (String word : line.split("\\W+")) {

            boolean ip_check = Pattern.matches("^[0-9]{1,3}$", word);

            if (ip_check) {

                forCnt++; // 반복횟수 증가
                ip += (word + "."); // IP값 저장하기

                // IP는 196.168.0.127 => 4가지 숫자로 조합되기에 반복은 1번부터 4번까지 IP 주소임
                if(forCnt == 4) {

                    // ip변수의 값은 192.168.0.127. 와 같이 마지막에 . 붙음
                    // 마지막 .을 제거하기 위해 0부터 마지막 위치에서 -1 값까지 문자열을 자름
                    ip = ip.substring(0, ip.length()-1);

                    // Suffle and Sort로 데이터를 전달하기
                    // 전달하는 값은 단어와 빈도수(1)를 전달함
                    context.write(new Text(ip), new IntWritable());

                }

            }

        }

    }

}
