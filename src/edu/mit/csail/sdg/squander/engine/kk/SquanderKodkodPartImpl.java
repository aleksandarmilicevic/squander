/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine.kk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import kodkod.ast.Expression;
import kodkod.ast.Relation;
import kodkod.engine.Solution;
import kodkod.engine.config.Options;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.engine.ISquander;
import edu.mit.csail.sdg.squander.engine.ISquanderResult.IEvaluator;
import edu.mit.csail.sdg.squander.utils.TupleSetUtils;
import forge.program.ForgeDomain;
import forge.program.ForgeExpression;
import forge.program.ForgeLiteral;
import forge.program.ForgeType;
import forge.program.ForgeVariable;
import forge.program.GlobalVariable;
import forge.program.InstanceDomain;
import forge.program.InstanceLiteral;
import forge.program.IntegerLiteral;
import forge.program.LocalVariable;
import forge.program.ForgeType.Unary;
import forge.solve.ForgeAtom;

/**
 * An implementation of the {@link ISquander} interface that
 * uses <i>KodkodPart</i> translation to Kodkod in order to 
 * minimize the number of atoms in the universe.
 * 
 * @author Aleksandar Milicevic
 */
public class SquanderKodkodPartImpl extends SquanderKodkodImpl {

    // ===========================================================================
    // --------------------------- KodkodIntEval ---------------------------------
    // ===========================================================================

    class KodkodIntEval extends KodkodEval {

        private ForgeVariable var;

        public KodkodIntEval(Iterator<Solution> solutions, Options options) {
            super(solutions, options);
        }

        @Override
        public ObjTupleSet evaluate(ForgeExpression expr) {
            if (expr instanceof ForgeVariable) {
                this.var = (ForgeVariable) expr;
                return super.evaluate(expr);
            } else {
                throw new RuntimeException("not supported by KodkodIntEval.  Use a different engine (e.g., SquanderKodkodImpl)");
            }
        }

        @Override
        protected ObjTupleSet makeConst(TupleSet ts) {
            assert var.arity() == ts.arity();
            ObjTupleSet res = new ObjTupleSet(ts.arity());
            for (kodkod.instance.Tuple t : ts) {
                ObjTuple fTuple = new ObjTuple();
                for (int i = 0; i < t.arity(); i++) {
                    Atom atom = (Atom) t.atom(i);
                    ForgeType.Unary type = (Unary) var.type().projectType(i);
                    ForgeLiteral lit = partitions.get(type).get(atom);
                    assert lit != null;
                    Object fAtom = fconv.lit2obj(lit);
                    fTuple = ObjTuple.product(fTuple, fAtom);
                }
                if (fTuple.arity() != 0)
                    res.add(fTuple);
            }
            return res;
        }

    }

    // ===========================================================================
    // ------------------------------- AtomEval ----------------------------------
    // ===========================================================================

    protected static class Atom {
        int key;

        Atom(int key) {
            this.key = key;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + key;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Atom other = (Atom) obj;
            if (key != other.key)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "a" + Integer.toString(key);
        }

    }

    // ========================================================

    protected Map<ForgeType.Unary, Map<Atom, ForgeLiteral>> partitions;
    protected Map<String, Atom> lit2atom;

    public SquanderKodkodPartImpl() { }

    private void sort(List<ForgeType.Unary> types) {
        Collections.sort(types, new Comparator<ForgeType.Unary>() {
            @Override
            public int compare(ForgeType.Unary o1, ForgeType.Unary o2) {
                int n1 = fconv.findLiteralsForType(o1).size();
                int n2 = fconv.findLiteralsForType(o2).size();
                return -(new Integer(n1).compareTo(n2));
            }
        });
    }

    protected void partitionDomains() {
        List<ForgeType.Unary> types = new ArrayList<ForgeType.Unary>(forgeScene.usedTypes());
        sort(types);

        partitions = new LinkedHashMap<ForgeType.Unary, Map<Atom, ForgeLiteral>>();
        lit2atom = new LinkedHashMap<String, Atom>();

        // first assign keys to integers
        for (ForgeLiteral lit : fconv.findLiteralsForType(program.integerDomain())) {
            int value = ((IntegerLiteral) lit).value();
            ints.add(value);
        }

        Map<ForgeDomain, Set<ForgeDomain>> deps = createDependencies(types);
        Iterator<Unary> it = types.iterator();
        Set<Atom> allAtoms = processFirst(it.next());
        while (it.hasNext()) {
            processNext(it.next(), deps, allAtoms);
        }
    }

    private void processNext(ForgeType.Unary t, Map<ForgeDomain, Set<ForgeDomain>> deps, Set<Atom> allAtoms) {
        Map<Atom, ForgeLiteral> map = new LinkedHashMap<Atom, ForgeLiteral>();
        Object x = partitions.put(t, map);
        assert x == null;
        List<ForgeLiteral> lits = fconv.findLiteralsForType(t);
        // firstly, process only those that already have assignments
        for (ForgeLiteral lit : lits) {
            Atom a = lit2atom.get(lit.name());
            if (a == null)
                continue;
            x = map.put(a, lit);
            assert x == null;
        }
        
        // compute set of available atoms
        Set<Atom> availAtoms = new HashSet<Atom>(allAtoms);
        availAtoms.removeAll(map.keySet());
        
        // assign values to the other ones
        for (ForgeLiteral lit : lits) {
            Atom a = lit2atom.get(lit.name());
            if (a != null)
                continue;
            a = nextAtomForLiteral(t, lit.type(), deps, new HashSet<Atom>(availAtoms));
            availAtoms.remove(a);
            lit2atom.put(lit.name(), a);
            x = map.put(a, lit);
            assert x == null;
        }
    }

    private Set<Atom> processFirst(ForgeType.Unary t) {
        Set<Atom> atoms = new HashSet<Atom>();
        HashMap<Atom, ForgeLiteral> at2lit = new HashMap<Atom, ForgeLiteral>();
        int idx = 0;
        for (ForgeLiteral lit : fconv.findLiteralsForType(t)) {
            Atom atom = new Atom(idx++);
            lit2atom.put(lit.name(), atom);
            at2lit.put(atom, lit);
            atoms.add(atom);
        }
        partitions.put(t, at2lit);
        return Collections.unmodifiableSet(atoms);
    }

    private Map<ForgeDomain, Set<ForgeDomain>> createDependencies(List<ForgeType.Unary> types) {
        Map<ForgeDomain, Set<ForgeDomain>> deps = new HashMap<ForgeDomain, Set<ForgeDomain>>();
        // NOT NECESSARY
//        for (ForgeType.Unary type : types) {
//            for (ForgeDomain t1 : type.tupleTypes()) {
//                if (!deps.containsKey(t1))
//                    deps.put(t1, new HashSet<ForgeDomain>());
//                for (ForgeDomain t2 : type.tupleTypes()) {
//                    if (t1 == t2)
//                        continue;
//                    deps.get(t1).add(t2);
//                }
//            }
//        }
        return deps;
    }

    protected Atom nextAtomForLiteral(Unary partition, ForgeDomain litType,
            Map<ForgeDomain, Set<ForgeDomain>> deps, Set<Atom> availAtoms) {
        // remove values already assigned to dependent domains (NOT NECESSARY)
//        for (ForgeDomain d : deps.get(litType)) {
//            for (ForgeLiteral l : fconv.findLiteralsForType(d)) {
//                Atom aa = lit2atom.get(l.name());
//                if (aa != null) {
//                    availAtoms.remove(aa);
//                }
//            }
//        }
        assert !availAtoms.isEmpty();
        return availAtoms.iterator().next();
    }

    @Override
    protected Universe createUniverse() {
        partitionDomains();
        //Log.log(printPartitions());
        Collection<Object> atoms = new LinkedList<Object>();
        for (Atom a : lit2atom.values()) {
            if (atoms.contains(a))
                continue;
            atoms.add(a);
        }
        return new Universe(atoms);
    }

    protected String printPartitions() {
        StringBuilder sb = new StringBuilder();
        for (Entry<Unary, Map<Atom, ForgeLiteral>> e : partitions.entrySet()) {
            sb.append("partition: " + e.getKey()).append("\n");
            for (Entry<Atom, ForgeLiteral> atoms : e.getValue().entrySet()) {
                sb.append(String.format("  %2s: %s\n", atoms.getKey(), atoms.getValue()));
            }
        }
        return sb.toString();
    }

    @Override
    protected Bounds createBounds() {
        reporter.creatingKodkodUniverse();
        Universe univ = createUniverse();
        
        reporter.creatingKodkodBounds();
        TupleFactory f = univ.factory();
        Bounds b = new Bounds(univ);
        
        // bound relations for literals
        for (Entry<String, Relation> e : lit2rel.entrySet()) {
            String litName = e.getKey();
            Atom atom = lit2atom.get(litName);
            assert atom != null;
            b.boundExactly(e.getValue(), f.setOf(atom));
        }
        
        // bound relations for instance domains
        for (InstanceDomain dom : program.instanceDomains()) {
            List<ForgeLiteral> instLiterals = fconv.findLiteralsForType(dom);
            TupleSet bound = f.noneOf(dom.arity());
            if (!instLiterals.isEmpty()) {
                Object[] atoms = new Object[instLiterals.size()];
                int idx = 0;
                for (ForgeLiteral lit : instLiterals) {
                    atoms[idx++] = lit2atom.get(lit.name());
                }
                bound = f.setOf(atoms);
            }
            b.boundExactly((Relation)type2expr.get(dom), bound);
        }

        // bound boolean
        Expression boolRel = type2expr.get(program.booleanDomain());
        if (boolRel != null) {
            b.boundExactly((Relation)boolRel, 
                    f.setOf(lit2atom.get(program.trueLiteral().name()), lit2atom.get(program.falseLiteral().name())));
        }
        
        // bound global variables
        for (GlobalVariable var : program.globalVariables()) {
            TupleSet[] lowup = getBounds(var, f);
            Relation rel = (Relation) var2rel.get(var.name());
            if (modifies.contains(var)) {
                Relation preRel = (Relation) var2rel.get(var.name() + "_pre");
                b.bound(preRel, lowup[0], lowup[1]);
                // TODO: get evaluate "instance modifies" spec
                TupleSet[] lu = getPostLowerUpper(var, lowup[0], f);
                b.bound(rel, lu[0], lu[1]);
            } else {
                b.bound(rel, lowup[0], lowup[1]);
            }
        }
        
        // bound relations for "this", "return" and method parameters
        boundLocalVar(forgeScene.thisVar(), f, b);
        for (LocalVariable var : forgeScene.args())
            boundLocalVar(var, f, b);
        boundLocalVar(forgeScene.returnVar(), f, b);

        // bound integers
        for (Integer i : ints) {
            Atom atom = lit2atom.get(Integer.toString(i));
            assert atom != null;
            b.boundExactly(i, f.setOf(atom));
        }

        return b;
    }
    
    private TupleSet[] getPostLowerUpper(GlobalVariable var, TupleSet lowerInitial, TupleFactory f) {
        TupleSet l = getPostLower(var, lowerInitial, f);
        TupleSet u = getPostUpper(var, l, f);
        return new TupleSet[] { l, u };
    }
    
    protected TupleSet getPostLower(GlobalVariable g, TupleSet initialBound, TupleFactory f) {
        ObjTupleSet instSelector = modVal.get(g);
        ObjTupleSet filter = lowerBounds.get(g);
		if (filter != null) {
			TupleSet res = f.noneOf(initialBound.arity());
			res.addAll(initialBound);
			res.removeAll(conv2tuples(filter, f));
			return res;
		} else if (instSelector != null) {
			List<ForgeLiteral> lits = new LinkedList<ForgeLiteral>(fconv
					.findLiteralsForType(g.type().domain()));
			for (ObjTuple t : instSelector.tuples()) {
				assert t.arity() == 1;
				if (t.arity() == 1) {
					InstanceLiteral l = forgeScene.instLitForObj(t.get(0));
					assert l != null;
					lits.remove(l);
				}
			}

			TupleSet res = f.noneOf(g.arity());
			for (ForgeLiteral lit : lits) {
				TupleSet ts = f.setOf(f.tuple(lit2atom.get(lit.name())));
				TupleSet filtered = ts.product(TupleSetUtils.join(ts, initialBound, f));
				res.addAll(filtered);
			}
			return res;
		} else {
			return f.noneOf(g.arity());
		}
    }
    
    protected TupleSet getPostUpper(GlobalVariable var, TupleSet postLower, TupleFactory f) {
        TupleSet extent = getExtent(var, f);
        ObjTupleSet upper = upperBounds.get(var);
        if (upper != null) 
            extent = conv2tuples(upper, f);
        ObjTupleSet ots = modVal.get(var); 
        if (ots == null)
            return extent; 
        TupleSet mod = conv2tuples(ots, f);
        mod = mod.product(TupleSetUtils.join(mod, extent, f));
        mod.addAll(postLower);
        return mod;
    }

    @Override
    protected void boundLocalVar(LocalVariable var, TupleFactory f, Bounds b) {
        if (var == null)
            return;
        TupleSet[] lowup = getBounds(var, f);
        Relation rel = (Relation) var2rel.get(relName(var));
        b.bound(rel, lowup[0], lowup[1]);
    }
    
    private TupleSet[] getBounds(ForgeVariable var, TupleFactory f) {
        TupleSet[] result = new TupleSet[2];
        ObjTupleSet bnd = fconv.heap2Lit().bounds().get(var.name());
        TupleSet lowerTupleSet;
        TupleSet upperTupleSet;
        if (bnd != null) {
            lowerTupleSet = conv2tuples(bnd, f);
            upperTupleSet = lowerTupleSet;
        } else {
            lowerTupleSet = f.noneOf(var.arity());
            upperTupleSet = getExtent(var, f);
        }
        result[0] = lowerTupleSet;
        result[1] = upperTupleSet;
        return result;
    }

    private TupleSet getExtent(ForgeVariable var, TupleFactory f) {
        // TODO: bound arrays tighter etc.
        ForgeType t = var.type();
        TupleSet result = null;
        for (int i = 0; i < t.arity(); i++) {
            ForgeType.Unary colType = (Unary) t.projectType(i);
            Map<Atom, ForgeLiteral> part = partitions.get(colType);
            assert part != null;
            TupleSet tuple = f.noneOf(1);
            for (Atom a : part.keySet()) {
                tuple.add(f.tuple(a));
            }
            if (result == null)
                result = tuple; 
            else 
                result = result.product(tuple);
        }
        if (result == null)
            result = f.noneOf(var.arity());
        return result;
    }

    protected TupleSet conv2tuples(ObjTupleSet fc, TupleFactory f) {
        List<kodkod.instance.Tuple> kkTuples = new LinkedList<kodkod.instance.Tuple>();
        l: for (ObjTuple t : fc.tuples()) {
            Object[] atoms = new Object[t.arity()];
            int idx = 0;
            for (Object obj : t.tuple()) {
                String litName = getLitNameForObject(obj);
                Atom atom = lit2atom.get(litName);
                if (atom == null)
                    continue l;
                atoms[idx++] = atom;
            }
            kkTuples.add(f.tuple(atoms));
        }
        if (kkTuples.isEmpty())
            return f.noneOf(fc.arity());
        else
            return f.setOf(kkTuples);
    }
    
    private String getLitNameForObject(Object obj) {
        if (obj == null)
            return "null";
        if (obj instanceof Integer)
            return Integer.toString((Integer) obj);
        if (obj instanceof Boolean) 
            return Boolean.toString((Boolean) obj);
        return forgeScene.instLitForObj(obj).name();
    }  
    
    @Override
    protected IEvaluator getEval(Iterator<Solution> solutions, Options options) {
        return new KodkodIntEval(solutions, options);
    }

    @Override
    protected Object convAtom(ForgeAtom a) {
        return lit2atom.get(a.name());
    }

}
/*! @} */