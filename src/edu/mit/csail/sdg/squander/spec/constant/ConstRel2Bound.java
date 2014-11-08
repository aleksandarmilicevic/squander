/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.engine.ForgeConverter;
import edu.mit.csail.sdg.squander.spec.ForgeScene;


public class ConstRel2Bound implements ConstRelVisitor<ObjTupleSet> {

    private final ForgeConverter fconv;
    private final ArrayList<Integer> ints;

    public ConstRel2Bound(ForgeConverter fconv) {
        this.fconv = fconv;
        this.ints = new ArrayList<Integer>(allInts());
        Collections.sort(ints);
    }

    @Override
    public ObjTupleSet visitDec() {
        ObjTupleSet fc = new ObjTupleSet(2);
        for (int i = 1; i < ints.size(); i++) 
            fc.add(new ObjTuple(ints.get(i), ints.get(i - 1)));
        return fc;
    }

    @Override
    public ObjTupleSet visitInc() {
        ObjTupleSet fc = new ObjTupleSet(2);
        for (int i = 0; i < ints.size() - 1; i++)
            fc.add(new ObjTuple(ints.get(i), ints.get(i + 1)));
        return fc;
    }

    @Override
    public ObjTupleSet visitIdent() {
        ObjTupleSet ots = new ObjTupleSet(2);
        for (Object obj : fconv.univ())
            ots.add(new ObjTuple(obj, obj));
        return ots;
    }

    @Override
    public ObjTupleSet visitNone() {
        return new ObjTupleSet(1); // TODO: other arities?
        //throw new RuntimeException("not implemented");
    }

    @Override
    public ObjTupleSet visitUniv() {
        ObjTupleSet ots = new ObjTupleSet(1);
        for (Object obj : fconv.univ()) 
            ots.add(new ObjTuple(obj));
        return ots;
    }
    
    protected Set<Integer> allInts() {
        Set<Integer> ints = new HashSet<Integer>();
        ForgeScene forgeScene = fconv.forgeScene();
        if (forgeScene.isEnsureAllInts()) {
            for (int i = fconv.minInt(); i <= fconv.maxInt(); i++)
                ints.add(i);
        } else {
            for (int i : forgeScene.ints())
                if (i >= fconv.minInt() && i <= fconv.maxInt())
                    ints.add(i);
        }
        return ints;
    }

}
/*! @} */
