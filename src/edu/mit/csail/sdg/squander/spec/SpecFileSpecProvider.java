/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.TreeAdaptor;

import edu.mit.csail.sdg.squander.spec.JType.Unary;
import edu.mit.csail.sdg.squander.spec.Source.Rule;
import edu.mit.csail.sdg.squander.specfile.parser.SpecFileLexer;
import edu.mit.csail.sdg.squander.specfile.parser.SpecFileParser;
import edu.mit.csail.sdg.squander.specfile.parser.SpecFileParserException;
import edu.mit.csail.sdg.squander.specfile.parser.SpecFileVisitor;
import edu.mit.csail.sdg.squander.specfile.parser.SpecFileParser.Node;


public class SpecFileSpecProvider implements ISpecProvider {

    class MyVisitor extends SpecFileVisitor<List<Source>> {

        private final JType.Unary jtype; 
        private final NameSpace ns;
        
        public MyVisitor(JType.Unary jtype) {
            this.jtype = jtype;
            this.ns = NameSpace.forClass(jtype);
        }

        @Override
        protected List<Source> visitInvariant(Node n) {
            List<Source> l = new LinkedList<Source>();
            for (int i = 0; i < n.getChildCount(); i++)
                l.add(new Source(trimQuotes(asText(n.getChild(i))), ns, Rule.CLAUSE));
            return l;
        }

        @Override
        protected List<Source> visitSpecField(Node n) {
            List<Source> l = new LinkedList<Source>();
            for (int i = 0; i < n.getChildCount(); i++)
                l.add(new Source(trimQuotes(asText(n.getChild(i))), ns, Rule.DECLARATION));
            return l;
        }
        
        @Override
        protected List<Source> visitFuncField(Node n) {
            List<Source> l = new LinkedList<Source>();
            for (int i = 0; i < n.getChildCount(); i++)
                l.add(new Source(trimQuotes(asText(n.getChild(i))), ns, Rule.DECLARATION, true));
            return l;
        }

        @Override
        protected List<Source> visitSpecFile(String modifiers, String name, List<String> paramNames, 
                List<List<Source>> specSources) {
            List<Source> src = new LinkedList<Source>();
            for (List<Source> l : specSources) {
                for (Source s : l) {
                    String srcString = replaceParamTypes(s.source, jtype, paramNames);
                    src.add(new Source(srcString, ns, s.rule));
                }
            }
            return src;
        }
    }

    @Override
    public List<Source> extractFieldSpec(Field field, JType.Unary declaringType) {
        return new LinkedList<Source>();
    }

    @Override
    public MethodSpec extractMethodSpec(Method method, NameSpace ns) {
        return null;
    }
    
    @Override
    public List<Source> extractClassSpec(JType.Unary jtype) {
        URL specFile = findSpecFile(jtype.clazz());
        if (specFile == null)
            return new LinkedList<Source>();
        Node n = parse(specFile);
        return new MyVisitor(jtype).visit(n);
    }

    private URL findSpecFile(Class<?> clazz) {
        if (clazz == null)
            return null;
        if (clazz.isArray()) {
            URL url = ClassLoader.getSystemResource("java/lang/Object[].jfspec");
            assert url != null : "could not find specs for arrays";
            return url;
        }
        String resName = clazz.getName().replaceAll("\\.", "/") + ".jfspec";
        URL systemResource = ClassLoader.getSystemResource(resName);
        return systemResource;
    }

    private Node parse(URL specFile) {
        try {
            SpecFileLexer lexer = new SpecFileLexer(new ANTLRInputStream(specFile.openStream()));
            CommonTokenStream tokens = new CommonTokenStream();
            tokens.setTokenSource(lexer);
            SpecFileParser parser = new SpecFileParser(tokens);
            TreeAdaptor adaptor = new SpecFileParser.NodeAdaptor();
            parser.setTreeAdaptor(adaptor);
            Object tree = parser.specfile().getTree();
            if (tree instanceof Node)
                return (Node) tree;
            throw new SpecFileParserException("Could not create AST: " + tree);
        } catch (IOException e) {
            throw new SpecFileParserException("Could not read input file", e);
        } catch (RecognitionException e) {
            throw new SpecFileParserException("Error parsing spec file", e);
        }
    }
    
    public static String replaceParamTypes(String source, Unary jtype, List<String> paramNames) {
        String result = source;
        String[] typeParamNames = new String[paramNames.size()];
        Class<?> clz = jtype.clazz();
        if (clz.isArray()) {
            assert paramNames.size() == 1;
            Class<?> compType = clz.getComponentType();
            if (compType.isArray())
                typeParamNames[0] = escapeClsName(compType.getCanonicalName());
            else
                typeParamNames[0] = escapeClsName(compType.getName());
        } else {
            for (int i = 0; i < typeParamNames.length; i++) {
                if (i < jtype.typeParams().length) {
                    typeParamNames[i] = escapeClsName(jtype.typeParams()[i].name());
                } else {
                    typeParamNames[i] = "Object";
                }
            }
        }
        for (int i = 0; i < paramNames.size(); i++) {
            result = result.replaceAll("\\b" + paramNames.get(i) + "\\b", "(" + typeParamNames[i] + ")");
        }
        return result;
    }

    private static String escapeClsName(String name) {
        return name.replaceAll("\\$", "\\\\\\$");
    }
    
}
/*! @} */
