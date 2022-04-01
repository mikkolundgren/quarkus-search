package demo.mikko;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

@ApplicationScoped
public class IndexerService {

    String KNN_DICT = "knn-dict";

    String apidocPath = "docs/";
    String apidocIndexPath = "index/";

    public IndexResult indexFiles() throws IOException {
        Date start = new Date();

        final Path apidocDir = Paths.get(apidocPath);
        Directory dir = FSDirectory.open(Paths.get(apidocIndexPath));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, indexWriterConfig);
        indexDocs(writer, apidocDir);
        writer.forceMerge(1);
        writer.commit();
        int docCount = writer.getDocStats().numDocs;
        Date end = new Date();
        long elapsed = end.getTime() - start.getTime();
        try (IndexReader reader = DirectoryReader.open(dir)) {
          System.out.println(
              "Indexed "
                  + reader.numDocs()
                  + " documents in "
                  + elapsed
                  + " milliseconds");
    
        }    
        return new IndexResult(docCount, elapsed);
    }

    void indexDocs(final IndexWriter writer, Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(
                    path,
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            try {
                                indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                            } catch (@SuppressWarnings("unused") IOException ignore) {
                                ignore.printStackTrace(System.err);
                                // don't index files that can't be read.
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
        }
    }

    /** Indexes a single document */
    void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {
            // make a new, empty document
            Document doc = new Document();

            // Add the path of the file as a field named "path". Use a
            // field that is indexed (i.e. searchable), but don't tokenize
            // the field into separate words and don't index term frequency
            // or positional information:
            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            doc.add(pathField);

            // Add the last modified date of the file a field named "modified".
            // Use a LongPoint that is indexed (i.e. efficiently filterable with
            // PointRangeQuery). This indexes to milli-second resolution, which
            // is often too fine. You could instead create a number based on
            // year/month/day/hour/minutes/seconds, down the resolution you require.
            // For example the long value 2011021714 would mean
            // February 17, 2011, 2-3 PM.
            doc.add(new LongPoint("modified", lastModified));

            // Add the contents of the file to a field named "contents". Specify a Reader,
            // so that the text of the file is tokenized and indexed, but not stored.
            // Note that FileReader expects the file to be in UTF-8 encoding.
            // If that's not the case searching for special characters will fail.
            doc.add(
                    new TextField(
                            "contents",
                            new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

            /*
             * if (demoEmbeddings != null) {
             * try (InputStream in = Files.newInputStream(file)) {
             * float[] vector =
             * demoEmbeddings.computeEmbedding(
             * new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)));
             * doc.add(
             * new KnnVectorField("contents-vector", vector,
             * VectorSimilarityFunction.DOT_PRODUCT));
             * }
             * }
             */

            // New index, so we just add the document (no old document can be there):
            System.out.println("adding " + file);
            writer.addDocument(doc);

        }
    }
}
