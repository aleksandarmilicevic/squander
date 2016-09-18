package edu.mit.csail.sdg.squander.examples.chameleon;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Macro;
import edu.mit.csail.sdg.annotations.Macros;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.engine.ExtraSkolems;
import edu.mit.csail.sdg.squander.examples.chameleon.Chameleons.Chameleon;
import edu.mit.csail.sdg.squander.examples.chameleon.Chameleons.State;
import edu.mit.csail.sdg.squander.examples.chameleon.Visual.Color;
import edu.mit.csail.sdg.squander.examples.chameleon.Visual.Projection;
import edu.mit.csail.sdg.squander.examples.chameleon.Visual.Shape;
import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;
import edu.mit.csail.sdg.squander.utils.VoidContinuation;

public class Theme {

    protected final Chameleons chameleons;
    protected Visual<State, Chameleon> visual; 
    
    public Theme(Chameleons chameleons) {
        this.chameleons = chameleons;
        init();
    }

    public Chameleons getChameleons()            { return chameleons; }
    public Visual<State, Chameleon> getVisual()  { return visual; }
       
    @Macros({
        @Macro(var = "PROJECTION", expr = "this.visual.projections.elts"),
        @Macro(var = "CHAMELEON", expr = "this.chameleons.chameleons.elts")
    })
    @Ensures({
        "all p : $PROJECTION | " +
           // colors for chameleons correspond to their kinds
           // two chameleons that meet each other have the same shape
        "  (all disj c1, c2 : $CHAMELEON | " +
        "    (p.projection.kind[c1] = p.projection.kind[c2] <=> p.color[p.atom.elts.c1] = p.color[p.atom.elts.c2]) && " +
        "    (c1 in p.projection.meets[c2]                   => p.shape[p.atom.elts.c1] = p.shape[p.atom.elts.c2])) &&" +
           // a chameleon doesn't meet anyone iff it has the box shape
        "  (all c : $CHAMELEON | " +
        "    no p.projection.meets[c] <=> p.shape[p.atom.elts.c] = Shape.Box) && " +
           // edges correspond to meets
        "  p.projection.meets.elts = (~((p.source.elts).(p.atom.elts))).((p.destination.elts).(p.atom.elts)) &&" +
           //
        "  p.color.keys = p.shape.keys = p.atom.keys",
        
        // stability between colors and kinds
        "all disj p1, p2: $PROJECTION | " +
        "  all disj c1, c2: $CHAMELEON | " +
        "    p1.projection.kind[c1] = p2.projection.kind[c2] => p1.color[p1.atom.elts.c1] = p2.color[p2.atom.elts.c2]",
        
        // stability between projections and atoms
        "all disj p1, p2: $PROJECTION | all c: $CHAMELEON | p1.atom.elts.c = p2.atom.elts.c",
    })
    @Modifies({
        // mappings from nodes to colors, shapes, atoms
        "Projection.color.elts", "Projection.shape.elts", //"Projection.atom.elts",
        // mappings from edges to source, destination
        "Projection.source.elts", "Projection.destination.elts",
    })
    //@Options(ensureAllInts=true, bitwidth=5)
    private void solveAll() {
        Squander.exe(this);
    }
    
    public void solve() {
        wrap("theme", new VoidContinuation() { public void exe() { solveAll(); }});
    }
    
    private void init() {
        visual = new Visual<State, Chameleon>();
        for (State s : chameleons.states) {
            Projection<State, Chameleon> p = new Projection<State, Chameleon>();
            visual.projections.add(p);
            p.projection = s;
        }
        for (Chameleon ch : chameleons.chameleons) {
            Visual.Node n = new Visual.Node();
            visual.allNodes.add(n);
            for (Projection<State, Chameleon> p : visual.projections) {
                p.atom.put(n, ch);
            }
        }
        int nCham = chameleons.chameleons.size();
        for (int i = 0; i < nCham * (nCham - 1) / 2; i++) {
            Visual.Edge e = new Visual.Edge();
            visual.allEdges.add(e);
        }
    }
    
    public static void main(String[] args) throws IOException {
        final int nChams; 
        final int nStates;
        String thKind = "theme";
        if (args.length > 0) thKind = args[0];
        if (args.length > 1) nChams = Integer.parseInt(args[1]); else nChams = 10;
        if (args.length > 2) nStates = Integer.parseInt(args[2]); else nStates = 11;

        long start = System.currentTimeMillis(); 
        SquanderGlobalOptions.INSTANCE.min_bitwidth = 4;
        SquanderGlobalOptions.INSTANCE.log_level = Level.NONE;
        //SquanderGlobalOptions.INSTANCE.log_level = Level.LOG;

        final Chameleons chs = new Chameleons();
        wrap("chameleons", new VoidContinuation() { public void exe() { chs.solve(nChams, nStates); }});
        //chs.print();
        
        Theme th = new Theme(chs);
        if ("theme2".equals(thKind)) th = new Theme2(chs);

        th.solve();
        
        long t = System.currentTimeMillis() - start;
        System.out.println(String.format("total time: %.2f", t/1000.0));
        
        generateAlloyVizInstance(th);
    }

    protected static void wrap(String name, VoidContinuation c) {
        System.out.println("solving " + name + "...");
        long start = System.currentTimeMillis(); 
        c.exe();
        long t = System.currentTimeMillis() - start;
        System.out.println(Squander.getLastResult().getStats());
        System.out.println(String.format("total time: %.2f", t/1000.0));
        System.out.println();
    }

    @Ensures({
        "some this.chameleons.chameleons.elts",
        "some this.chameleons.states.elts.(kind+meets)",
        "some this.visual.projections.elts.(projection+color+shape+atom+source+destination)", 
    })
    // used for the purpose of visualization
    private void dummy() { Squander.exe(this); }
    
    protected static void generateAlloyVizInstance(final Theme th) throws IOException {
        th.dummy();
        File f = new File("myinst.xml");
        PrintStream ps = new PrintStream(f); //System.out; 
             
        // add skolem functions for vizualization
        List<String> skolemFunctions = new ArrayList<String>(20);
        skolemFunctions.add("pkind:  {p: Projection, c: Chameleon, k: Kind | p.projection.kind[c] = k}");
        skolemFunctions.add("pmeets: {p: Projection, c: Chameleon, c2: Chameleon | p.projection.meets[c] = c2}");
        skolemFunctions.add("prOrd:  {v: Visual<State, Chameleon>, pos: int, p: Projection | this.chameleons.states[pos] = p.projection}");
        skolemFunctions.add("edges:  {p: Projection, n1: Node, n2: Node | some e: Edge | p.source[e] = n1 && p.destination[e] = n2}");
        for (Color e : Color.values()) {
            for (Shape s : Shape.values()) {
                String fname = e.name() + s.name();
                String fmt = "%s: {p: Projection, n: Node | p.color[n] = Color.%s && p.shape[n] = Shape.%s}";
                skolemFunctions.add(String.format(fmt, fname, e.name(), s.name()));
            }
        }
        
        Squander.getLastResult().exportToAlloyVizInst(ps, new ExtraSkolems(skolemFunctions.toArray(new String[0])), 
                new Comparator<Object>() {
                    @SuppressWarnings({ "rawtypes", "unchecked" })
                    @Override
                    public int compare(Object o1, Object o2) {
                        if (o1 instanceof Projection && o2 instanceof Projection) {
                            Projection<State, Chameleon> p1 = (Projection) o1;
                            Projection<State, Chameleon> p2 = (Projection) o2;
                            int idx1 = idxOf(th.chameleons.states, p1.projection);
                            int idx2 = idxOf(th.chameleons.states, p2.projection);
                            return new Integer(idx1).compareTo(idx2);
                        }
                        return 0;
                    }

                    private int idxOf(State[] states, State s) {
                        for (int i = 0; i < states.length; i++) 
                            if (states[i] == s)
                                return i;
                        return -1;
                    }
                });
        System.out.println("instance written in: " + f.getAbsolutePath());
    }
        
}
