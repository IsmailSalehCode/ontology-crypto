import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class App {
    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
    }
    public static void main(String[] args) {
        String owlFile = "crypto.owl";

        OntModel model = ModelFactory.createOntologyModel();
        FileManager.get().readModel(model, owlFile);

        String SPARQL_prefixes="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + "PREFIX crypto: <http://www.semanticweb.org/ismail/ontologies/2024/0/crypto#>"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>";

        String SPARQL_query="SELECT ?blockCipher ?encryption ?decryption ?securityStrength\n" +
                "WHERE {\n" +
                " ?blockCipher rdf:type crypto:BlockCipher .\n" +
                " ?blockCipher crypto:isEncryption ?encryption .\n" +
                " ?blockCipher crypto:isDecryption ?decryption .\n" +
                " ?blockCipher crypto:securityStrength ?securityStrength .\n" +
                "}";
        String full_query = SPARQL_prefixes+SPARQL_query;

        Query query = QueryFactory.create(full_query);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        String queryType=getQueryType(SPARQL_query);

        if ("ASK".equalsIgnoreCase(queryType)) {
            printAskQueryResult(queryExecution);
        } else if ("SELECT".equalsIgnoreCase(queryType)) {
            printSelectQueryResult(queryExecution);
        } else {
            System.out.println("Unsupported query type: " + queryType);
        }
    }

    private static String getQueryType(String sparqlQuery) {
        String[] words = sparqlQuery.trim().split("\\s+");
        return (words.length > 0) ? words[0] : "";
    }

    private static void printAskQueryResult(QueryExecution q) {
        try {
            boolean result = q.execAsk();
            System.out.println("ASK Query result: " + result);
        } finally {
            q.close();
        }
    }

    private static void printSelectQueryResult(QueryExecution q) {
        try {
            ResultSet resultSet = q.execSelect();
            System.out.println("SELECT Query result: ");
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                System.out.println( solution);
            }
        } finally {
            q.close();
        }
    }
}
