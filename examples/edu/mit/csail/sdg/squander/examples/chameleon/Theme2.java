package edu.mit.csail.sdg.squander.examples.chameleon;

import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FuncField;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.errors.NoSolution;
import edu.mit.csail.sdg.squander.examples.chameleon.Chameleons.Chameleon;
import edu.mit.csail.sdg.squander.examples.chameleon.Chameleons.Kind;
import edu.mit.csail.sdg.squander.examples.chameleon.Visual.Color;
import edu.mit.csail.sdg.squander.examples.chameleon.Visual.Node;
import edu.mit.csail.sdg.squander.examples.util.NNMap;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;
import edu.mit.csail.sdg.squander.utils.VoidContinuation;

@FuncField("pmeets: Projection<State,Chameleon> -> Chameleon -> Chameleon")
public class Theme2 extends Theme {

    public NNMap<Chameleon, Node> c2n = new NNMap<Chameleon, Node>();
    public NNMap<Kind, Color> k2c = new NNMap<Kind, Color>();
    
    public Theme2(Chameleons chameleons) {
        super(chameleons);
    }
  
    @Requires("chameleons.length = nodes.length")
    @Ensures({
        "this.c2n.keys = chameleons.elts",
        "this.c2n.vals = nodes.elts"
    })
    @Modifies("this.c2n.elts")
    private void solveC2N(Set<Chameleon> chameleons, Set<Node> nodes) { Squander.exe(this, chameleons, nodes); }
   
    @Requires("#Kind <= #Color")
    @Ensures({
        "this.k2c.keys = Kind",
        "#this.k2c[Kind] = #Kind"
    })
    @Modifies("this.k2c.elts")
    private void solveK2C() { Squander.exe(this); }
    
    @Ensures({
        "all p: this.visual.projections.elts | ("
      + "  p.shape.keys = Node && " 
      + "  (all c: this.chameleons.chameleons.elts | "
             // shape == Shape.Box IFF it meets no one     
      + "    (no p.projection.meets[c] <=> p.shape[this.c2n[c]] = Shape.Box)"
             // those who meet have the same shapes
      + "    && (some p.projection.meets[c] => p.shape[this.c2n[c]] = p.shape[this.c2n[p.projection.meets[c]]])"
             // remap colors and atoms from k2c and c2n
      + "    && p.color[this.c2n[c]] = this.k2c[p.projection.kind[c]]"
      + "    && p.atom[this.c2n[c]] = c)"
      + ")"
    })
    @Modifies({
        "Projection.color.elts", "Projection.shape.elts", "Projection.atom.elts",
    })
    private void solveNodes() { Squander.exe(this); }
    
//    @Ensures({
//        "all p: this.visual.projections.elts |" +
//        "  p.projection.meets.elts = (~((p.source.elts).(p.atom.elts))).((p.destination.elts).(p.atom.elts))"
//    })

//    @Ensures({
//       "some this.visual.projections.elts",
//       "all p: Projection |" +
//       "  all c: Chameleon | " 
//     + "    (some p.projection.meets[c] => (some p.source.elts.(this.c2n[c]) && p.source.elts.(this.c2n[c]) = p.destination.elts.(this.c2n[p.projection.meets[c]]))) &&" 
//     + "    (no p.projection.meets[c] => no p.(source+destination).elts.(this.c2n[c]))"
//    })

    @Ensures({
       "some this.visual.projections.elts",
       "all pcc: Projection<State,Chameleon>@projection.meets.elts | " +
       "  some e: Edge | e = pcc<0>.source.elts.(this.c2n[pcc<1>]) = pcc<0>.destination.elts.(this.c2n[pcc<2>])",
   
       "all pc: {p: Projection, c: Chameleon | no p.projection.meets[c]} | " +
       "  no pc<0>.(source+destination).elts.(this.c2n[pc<1>])"
     })
    
    @Modifies({
        "Projection.source.elts", "Projection.destination.elts"
    })
    private void solveEdges() {
        SquanderGlobalOptions.INSTANCE.pre_eval = true;
        Squander.exe(this); 
    }
    
    public void solve() {
        try {
//            SquanderGlobalOptions.INSTANCE.log_level = Level.DEBUG;
//            SquanderGlobalOptions.INSTANCE.unsat_core = true;
//            SquanderGlobalOptions.INSTANCE.sat_solver = SATFactory.MiniSatProver;
            wrap("Chameleon -> Node ", new VoidContinuation() { public void exe() { solveC2N(chameleons.chameleons, visual.allNodes); }});
            wrap("Kind -> Color",      new VoidContinuation() { public void exe() { solveK2C();} });
            wrap("nodes",              new VoidContinuation() { public void exe() { solveNodes();} });
            wrap("edges",              new VoidContinuation() { public void exe() { solveEdges();} });
        } catch (NoSolution e) {
            System.out.println(Squander.getLastResult().unsatCore());
        }
    }
    

}
