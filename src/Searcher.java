import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import javax.print.Doc;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

public class Searcher {

    QueryParser queryParser;

    public Searcher(String indexDirectoryPath, Analyzer analyzer, Set<String> hash_set) throws IOException, ParseException {
        File resultFile = new File("result.txt");
        FileWriter writer = new FileWriter("result.txt");
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
        IndexReader reader = DirectoryReader.open(indexDirectory);


        IndexSearcher searcher = new IndexSearcher(reader);
        queryParser = new QueryParser("content",analyzer);
        ArrayList<Integer> relevant=new ArrayList();

        relevant.add(5);
        relevant.add(6);
        relevant.add(91);
        relevant.add(90);
        relevant.add(119);
        relevant.add(144);
        relevant.add(181);
        relevant.add(399);
        relevant.add(485);
        int fp;
        int fn;
        int tp=0;

       // for(String queryString : hash_set){
            String queryString="what problems of heat conduction in composite slabs have been solved so far .";
            Query q=queryParser.parse(queryString);
            TopDocs td = searcher.search(q,Integer.MAX_VALUE);
            ScoreDoc[] sd = td.scoreDocs;
//            System.out.print(queryString+": ");
 //           writer.append(queryString + ": ");
           for (int j = 0 ; j< sd.length ; j++){
//                Document document = reader.document(sd[j].doc);
//                String fieldContent = document.get("content");
////                System.out.println(fieldContent);
//                int count = countOccurences(fieldContent,queryString);
////                System.out.print("doc"+sd[j].doc+"["+count+"]");
//                writer.append("doc" + sd[j].doc + "[" + count + "]");
//                if(j < sd.length-1)  {
//                   // writer.append(", ");
////                  System.out.print(", ");
//                }
               if(relevant.contains(sd[j].doc)){
                   tp++;
               }
            }
            fp=sd.length-tp;
            fn=relevant.size()-tp;
            double precision=((double) tp/(double)(tp+fp));
            double recall=((double) tp/(double)(tp+fn));
            System.out.println("total retrieved = "+sd.length);
            System.out.println("fp = "+fp);
            System.out.println("tp = "+tp);
            System.out.println("fn = "+fn);
            System.out.println("precision = "+precision);
            System.out.println("recall = "+recall);

            writer.append("\n");
       // }
        writer.close();
    }

    static int countOccurences(String str, String word) {
//        // split the string by spaces in a
        String a[] = str.split(" ");
//
//        // search for pattern in a
        int count = 0;
        for (int i = 0; i < a.length; i++)
        {
            // if match found increase count
            if (word.equals(a[i]))
                count++;
        }
        return count;
    }
}
