package com.mgtechno.shared.index;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LuceneIndexingService {
    private static final Logger LOG = LoggerFactory.getLogger(LuceneIndexingService.class);

    private List<IndexingFieldDetail> fields;
    private IndexWriter writer;
    private String indexPath;
    private boolean createOnly;
    private SearcherManager searcherManager;
    private PerFieldAnalyzerWrapper fieldAnalyzerWrapper;

    public LuceneIndexingService(String indexPath, boolean createOnly, List<IndexingFieldDetail> fields) throws Exception {
        this.indexPath = indexPath;
        this.createOnly = createOnly;
        this.fields = fields;
        Map<String, Analyzer> fieldAnalyzerMap = fields.stream().filter(field -> field.getAnalyzers() != null)
                .collect(Collectors.toMap(field -> field.getName(), field -> field.getAnalyzers().getAnalyzer()));
        this.fieldAnalyzerWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), fieldAnalyzerMap);
    }

    private SearcherManager getSearcherManager()throws Exception{
        if(searcherManager == null){
            searcherManager = new SearcherManager(FSDirectory.open(Paths.get(indexPath)), null);
        }
        return searcherManager;
    }

    public <R> List<R> findAddressBy(Class<R> returnType, IndexingFieldDetail field, String searchValue, int count,
                                     IndexingFieldDetail.QueryType queryType) throws Exception {
        List<R> result = new ArrayList<>();
        IndexSearcher searcher = getSearcherManager().acquire();
        try {
            TopDocs topDocs = searcher.search(getQuery(field, searchValue, queryType), count);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc: hits) {
                R object = returnType.newInstance();
                BeanUtilsBean beanUtil = new BeanUtilsBean();
                Document document = searcher.doc(scoreDoc.doc);
                fields.forEach(indexingFeild -> {
                    try {
                        beanUtil.setProperty(object, indexingFeild.getName(), document.get(indexingFeild.getName()));
                    } catch (Exception e) {
                        LOG.error("failed set property " + field.getName() + " on result object");
                    }
                });
                result.add(object);
            }
        }finally {
            getSearcherManager().release(searcher);
        }
        return result;
    }

    private Query getQuery(IndexingFieldDetail field, String search, IndexingFieldDetail.QueryType queryType) throws ParseException {
        Query query = null;
        switch(queryType) {
            case TERM:
                query = new TermQuery(new Term(field.getName(), search));
                break;
            case PHRASE:
                query = new PhraseQuery(field.getName(), search);
                break;
            case FUZZY:
                query = new FuzzyQuery(new Term(field.getName(), search), field.getMaxEdits());
                break;
            default:
                QueryParser parser = new QueryParser(field.getName(), new StandardAnalyzer());
                query = parser.parse(search);
        }
        return query;
    }

    private IndexWriter getWriter(IndexWriterConfig.OpenMode openMode) throws Exception {
        if(writer == null || !writer.isOpen()) {
            IndexWriterConfig iwc = new IndexWriterConfig(fieldAnalyzerWrapper);
            iwc.setOpenMode(openMode);
            writer = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), iwc);
        }
        return writer;
    }

    public IndexWriter getWriter(){
        return writer;
    }

    public <T> void addIndex(T object, IndexingFieldDetail documentIdFeild, IndexWriterConfig.OpenMode openMode)throws Exception{
        Document document = new Document();
        BeanUtilsBean beanUtil = new BeanUtilsBean();
        for(IndexingFieldDetail field : fields){
            String value = beanUtil.getProperty(object, field.getName());
            Field.Store store = field.isStore() ? Field.Store.YES : Field.Store.NO;
            if(StringUtils.isNotEmpty(value) && field.getType().equals(IndexingFieldDetail.FieldType.STRING)) {
                document.add(new StringField(field.getName(), value, store));
            }else if(StringUtils.isNotEmpty(value) && field.getType().equals(IndexingFieldDetail.FieldType.TEXT)) {
                document.add(new TextField(field.getName(), value, store));
            }
        }

        IndexWriter writer = getWriter(openMode);
        if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
            writer.addDocument(document);
        } else {
            writer.updateDocument(new Term(documentIdFeild.getName(), beanUtil.getProperty(object, documentIdFeild.getName())), document);
        }
    }
}
