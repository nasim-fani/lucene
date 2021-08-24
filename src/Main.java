import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.util.*;

public class Main {
    static String indexDir = "D:\\in term7\\Bazyabi\\3\\indexDir1";
    static String dataDir = "D:\\in term7\\Bazyabi\\3\\33\\Docs.txt";
    //    Indexer indexer;
    static Set<String> hash_Set = new HashSet<String>();
    public static void main(String[] args){
        String content = null;
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            System.out.print("please inter index directory path:: ");
            indexDir = reader.readLine();
            System.out.print("please inter text file path for indexing:: ");
            dataDir = reader.readLine();

            Analyzer analyzer = CustomAnalyzer.builder()
                    .withTokenizer("standard")
                    .addTokenFilter("lowercase")
                    .addTokenFilter("stop")
                    .addTokenFilter("porterstem")
//                    .addTokenFilter("capitalization")
                    .build();
            content = readFile(dataDir, StandardCharsets.UTF_8);
           ArrayList<Document> docs = getDocuments(content,analyzer);

            Analyzer analayzer2 = new WhitespaceAnalyzer();
            analyze(docs,analayzer2);
            Searcher s = new Searcher(indexDir,analyzer,hash_Set);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    public static void analyze(ArrayList<Document> documents, Analyzer analyzer){
        try{
            Directory dir = FSDirectory.open(Paths.get(indexDir));

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter writer = new IndexWriter(dir, iwc);
            for(int i=0 ; i<documents.size() ; i++){
                writer.addDocument(documents.get(i));
                if(i==0) iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            }
            writer.close();
        }catch(IOException e){
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }
    public static ArrayList<Document> getDocuments(String content,Analyzer analyzer){
        Scanner scan = new Scanner(content);
        ArrayList<Document> docs = new ArrayList<>();
        int i=0;
        String str = "";
        while (scan.hasNextLine()){
            String readLine = scan.nextLine();
            if(readLine.contains(".I "+(i+1))){
                if(i!=0) {
                    String stringAnalyzed = getAnalyzedString(analyzer,str);
                    Document doc = new Document();
                    doc.add(new TextField("content",stringAnalyzed,Field.Store.YES));
                    docs.add(doc);
                }
                str = "";
                i++;
            }
           else if(!readLine.contains("I "+(i+1)) && !readLine.contains(".W") && !readLine.contains(".T") && !readLine.contains(".A") && !readLine.contains(".B")){
                str+=" "+readLine;
           }
        }

        //last paragraph remain so it must be documented
        Document doc = new Document();
        String stringAnalyzed = getAnalyzedString(analyzer,str);
        doc.add(new TextField("content",stringAnalyzed,Field.Store.YES));
        docs.add(doc);
        return docs;
    }
    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    public static String getAnalyzedString(Analyzer analyzer,String content){
        TokenStream tokenStream = analyzer.tokenStream("content",content);
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        String stringAnalyzed = "";
        try{
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                int startOffset = offsetAttribute.startOffset();
                int endOffset = offsetAttribute.endOffset();
                String term = charTermAttribute.toString();
                hash_Set.add(term);
//              System.out.println(term);
                stringAnalyzed+=term+" ";
            }
            tokenStream.close();
        }catch (IOException e){
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
        return stringAnalyzed;
    }
}



