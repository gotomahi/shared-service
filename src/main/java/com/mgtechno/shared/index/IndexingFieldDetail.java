package com.mgtechno.shared.index;

import lombok.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.Serializable;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndexingFieldDetail implements Serializable {
    private String name;
    private boolean store;
    private FieldType type;
    private Analyzers analyzers;
    private QueryType queryType;
    private int maxEdits = 2;

    public IndexingFieldDetail(String name, boolean store, FieldType type, Analyzers analyzers) {
        this.name = name;
        this.store = store;
        this.type = type;
        this.analyzers = analyzers;
    }

    public enum FieldType{
        STRING(1), TEXT(2);
        private int value;
        FieldType(int value){
            this.value = value;
        }
    }
    public enum Analyzers {
        //It is the most commonly used analyzer.can recognize URLs and emails.
        //Also, it removes stop words and lowercases the generated tokens
        STANDARD(new  StandardAnalyzer()),
        //It tokenizes input into a single token and useful for id's and zipcodes
        KEYWORD(new KeywordAnalyzer());

        private Analyzer analyzer;
        Analyzers(Analyzer analyzer){
            this.analyzer = analyzer;
        }
        public Analyzer getAnalyzer(){
            return analyzer;
        }
    }

    public enum QueryType {
        PHRASE("Phrase"), TERM("Term"), FUZZY("Fuzzy"), QUERY("QueryParser");
        private String query;
        QueryType(String query){
            this.query = query;
        }
    }
}
