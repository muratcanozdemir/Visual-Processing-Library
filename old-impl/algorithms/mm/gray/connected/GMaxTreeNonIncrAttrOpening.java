/*   1:    */ package vpt.algorithms.mm.gray.connected;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Iterator;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.mm.gray.connected.attribute.Attribute;
/*   9:    */ import vpt.algorithms.mm.gray.connected.maxtree.MaxTree;
/*  10:    */ import vpt.algorithms.mm.gray.connected.maxtree.Node;
/*  11:    */ 
/*  12:    */ public class GMaxTreeNonIncrAttrOpening
/*  13:    */   extends Algorithm
/*  14:    */ {
/*  15: 23 */   public Image input = null;
/*  16: 24 */   public Attribute crit = null;
/*  17: 25 */   public Image output = null;
/*  18: 26 */   public Integer filteringStrategy = null;
/*  19:    */   public static final int DIRECT = 0;
/*  20:    */   public static final int SUBTRACTIVE = 1;
/*  21:    */   public static final int MIN = 2;
/*  22:    */   public static final int MAX = 3;
/*  23:    */   
/*  24:    */   public GMaxTreeNonIncrAttrOpening()
/*  25:    */   {
/*  26: 46 */     this.inputFields = "input, crit, filteringStrategy";
/*  27: 47 */     this.outputFields = "output";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void execute()
/*  31:    */     throws GlobalException
/*  32:    */   {
/*  33: 52 */     Node root = MaxTree.invoke(this.input);
/*  34: 53 */     if (root == null) {
/*  35: 53 */       throw new GlobalException("Tree is null..WTF");
/*  36:    */     }
/*  37: 56 */     if (this.filteringStrategy == null) {
/*  38: 56 */       this.filteringStrategy = Integer.valueOf(3);
/*  39:    */     }
/*  40: 58 */     if (this.filteringStrategy.intValue() == 0)
/*  41:    */     {
/*  42: 63 */       if (!this.crit.test(root.pixels, this.input)) {
/*  43: 63 */         root = null;
/*  44:    */       } else {
/*  45: 64 */         processDirectlyNode(root);
/*  46:    */       }
/*  47:    */     }
/*  48: 66 */     else if (this.filteringStrategy.intValue() == 2)
/*  49:    */     {
/*  50: 73 */       if (!this.crit.test(root.pixels, this.input)) {
/*  51: 73 */         root = null;
/*  52:    */       } else {
/*  53: 74 */         processNodeByMinRule(root);
/*  54:    */       }
/*  55:    */     }
/*  56: 76 */     else if (this.filteringStrategy.intValue() == 3) {
/*  57: 80 */       root = processNodeByMaxRule(root, null);
/*  58:    */     } else {
/*  59: 82 */       throw new GlobalException("Invalid filtering strategy");
/*  60:    */     }
/*  61: 85 */     this.output = MaxTree.tree2Image(root, this.input.getXDim(), this.input.getYDim());
/*  62:    */   }
/*  63:    */   
/*  64:    */   private Node processNodeByMaxRule(Node node, Iterator<Node> parentsIterator)
/*  65:    */   {
/*  66: 98 */     if (node.children.size() > 0)
/*  67:    */     {
/*  68:101 */       Iterator<Node> ite = node.children.iterator();
/*  69:103 */       while (ite.hasNext()) {
/*  70:104 */         processNodeByMaxRule((Node)ite.next(), ite);
/*  71:    */       }
/*  72:    */     }
/*  73:113 */     if (node.children.size() == 0) {
/*  74:115 */       if (!this.crit.test(node.pixels, this.input)) {
/*  75:118 */         if (parentsIterator != null) {
/*  76:118 */           parentsIterator.remove();
/*  77:    */         } else {
/*  78:119 */           return null;
/*  79:    */         }
/*  80:    */       }
/*  81:    */     }
/*  82:123 */     return node;
/*  83:    */   }
/*  84:    */   
/*  85:    */   private void processNodeByMinRule(Node node)
/*  86:    */   {
/*  87:130 */     Iterator<Node> ite = node.children.iterator();
/*  88:133 */     while (ite.hasNext())
/*  89:    */     {
/*  90:136 */       Node n = (Node)ite.next();
/*  91:139 */       if (!this.crit.test(n.pixels, this.input)) {
/*  92:143 */         ite.remove();
/*  93:    */       }
/*  94:    */     }
/*  95:148 */     ite = node.children.iterator();
/*  96:150 */     while (ite.hasNext()) {
/*  97:151 */       processNodeByMinRule((Node)ite.next());
/*  98:    */     }
/*  99:    */   }
/* 100:    */   
/* 101:    */   private void processDirectlyNode(Node node)
/* 102:    */   {
/* 103:158 */     Iterator<Node> ite = node.children.iterator();
/* 104:160 */     while (ite.hasNext())
/* 105:    */     {
/* 106:163 */       Node n = (Node)ite.next();
/* 107:166 */       if (!this.crit.test(n.pixels, this.input))
/* 108:    */       {
/* 109:170 */         ite.remove();
/* 110:    */         
/* 111:    */ 
/* 112:173 */         node.children.addAll(n.children);
/* 113:176 */         for (Node m : n.children) {
/* 114:177 */           m.parent = node;
/* 115:    */         }
/* 116:    */       }
/* 117:    */     }
/* 118:182 */     ite = node.children.iterator();
/* 119:184 */     while (ite.hasNext()) {
/* 120:185 */       processDirectlyNode((Node)ite.next());
/* 121:    */     }
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static Image invoke(Image input, Attribute c)
/* 125:    */   {
/* 126:    */     try
/* 127:    */     {
/* 128:190 */       return (Image)new GMaxTreeNonIncrAttrOpening().preprocess(new Object[] { input, c, null });
/* 129:    */     }
/* 130:    */     catch (GlobalException e)
/* 131:    */     {
/* 132:192 */       e.printStackTrace();
/* 133:    */     }
/* 134:193 */     return null;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public static Image invoke(Image input, Attribute c, Integer filteringStrategy)
/* 138:    */   {
/* 139:    */     try
/* 140:    */     {
/* 141:199 */       return (Image)new GMaxTreeNonIncrAttrOpening().preprocess(new Object[] { input, c, filteringStrategy });
/* 142:    */     }
/* 143:    */     catch (GlobalException e)
/* 144:    */     {
/* 145:201 */       e.printStackTrace();
/* 146:    */     }
/* 147:202 */     return null;
/* 148:    */   }
/* 149:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.GMaxTreeNonIncrAttrOpening
 * JD-Core Version:    0.7.0.1
 */