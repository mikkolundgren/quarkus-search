package demo.mikko;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

@ApplicationScoped
public class SearchService {
    
    String apidocIndexPath = "index/";
    String field = "contents";
    DirectoryReader reader;
    IndexSearcher searcher;
    Analyzer analyzer;

    public SearchService() {
        try {
            reader = DirectoryReader.open(FSDirectory.open(Paths.get(apidocIndexPath)));
            searcher = new IndexSearcher(reader);
            analyzer = new StandardAnalyzer();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    SearchResult doQuery(String inputQuery, int max, int offset) throws ParseException, IOException {
        QueryParser queryParser = new QueryParser("contents", analyzer);
        Query query = queryParser.parse(inputQuery);
        TopDocs topDocs = searcher.search(query, 200);
        int numTotalHits = Math.toIntExact(topDocs.totalHits.value);
        System.out.println(numTotalHits + " total matching documents");
        ScoreDoc[] hits = topDocs.scoreDocs;
        List<SearchHit> searchHits = new ArrayList<>();
        int numberReturned = max < numTotalHits ? max : numTotalHits;
        int startPos = 0;
        if (offset > 0) {
            if ((offset + max) < numTotalHits) {
                startPos = offset;
                numberReturned+= offset;
            } else {
                startPos = offset;
            }

        }
        for (int i = startPos; i < numberReturned; i++) {
            System.out.println("doc=" + hits[i].doc + " score=" + hits[i].score);
            searchHits.add(new SearchHit(hits[i].score, searcher.doc(hits[i].doc).get("path")));

        }
        SearchResult searchResult = new SearchResult();
        searchResult.setTotalHits(numTotalHits);
        searchResult.setSearchHits(searchHits);
        searchResult.setMax(max);
        searchResult.setOffset(offset);
        return searchResult;
    }   

    SearchResult searchResult = new SearchResult();

    @PreDestroy
    void destroyed() {
        try {
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
