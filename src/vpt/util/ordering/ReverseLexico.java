package vpt.util.ordering;

import vpt.util.Tools;

/**
 * Reconstructed: referenced by QuantaLexLSH but absent from the published
 * decompile. Mirrors Lexico with reversed channel priority (compares the
 * last component first), which is the standard "reverse lexicographic"
 * vector ordering.
 */
public class ReverseLexico extends AlgebraicOrdering {

    public int compare(double[] v1, double[] v2) {
        for (int i = v1.length - 1; i >= 0; i--) {
            int tmp = Tools.cmpr(v1[i], v2[i]);
            if (tmp != 0) {
                return tmp;
            }
        }
        return 0;
    }
}
