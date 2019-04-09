/*   1:    */ package vpt.algorithms.mm.gray.connected.maxtree;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import vpt.Algorithm;
/*   8:    */ import vpt.ByteImage;
/*   9:    */ import vpt.GlobalException;
/*  10:    */ import vpt.Image;
/*  11:    */ import vpt.IntegerImage;
/*  12:    */ import vpt.util.HierarchicalQueue;
/*  13:    */ import vpt.util.Tools;
/*  14:    */ 
/*  15:    */ public class MaxTree
/*  16:    */   extends Algorithm
/*  17:    */ {
/*  18: 31 */   public Image input = null;
/*  19: 32 */   public Node root = null;
/*  20: 34 */   private HierarchicalQueue queue = null;
/*  21: 35 */   private Node[][] nodes = null;
/*  22: 36 */   private ArrayList<Point> S = null;
/*  23: 37 */   private Point[] levroot = null;
/*  24: 38 */   private Image parent = null;
/*  25: 40 */   private final int NOT_ANALYZED = -1;
/*  26: 41 */   private final int IN_THE_QUEUE = -2;
/*  27: 42 */   private final int GRAY_LEVELS = 256;
/*  28:    */   private int xdim;
/*  29:    */   private int ydim;
/*  30: 47 */   private final boolean DEBUG = false;
/*  31:    */   
/*  32:    */   public MaxTree()
/*  33:    */   {
/*  34: 50 */     this.inputFields = "input";
/*  35: 51 */     this.outputFields = "root";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void execute()
/*  39:    */     throws GlobalException
/*  40:    */   {
/*  41: 56 */     if (this.input.getCDim() != 1) {
/*  42: 56 */       throw new GlobalException("Only grayscale images for now");
/*  43:    */     }
/*  44: 57 */     this.xdim = this.input.getXDim();
/*  45: 58 */     this.ydim = this.input.getYDim();
/*  46:    */     
/*  47: 60 */     this.levroot = new Point[256];
/*  48: 61 */     Arrays.fill(this.levroot, null);
/*  49:    */     
/*  50: 63 */     this.parent = new IntegerImage(this.input, false);
/*  51: 64 */     this.parent.fill(-1);
/*  52:    */     
/*  53:    */ 
/*  54: 67 */     this.S = new ArrayList();
/*  55:    */     
/*  56:    */ 
/*  57:    */ 
/*  58: 71 */     this.nodes = new Node[this.ydim][this.xdim];
/*  59:    */     
/*  60:    */ 
/*  61: 74 */     this.queue = new HierarchicalQueue(256);
/*  62:    */     
/*  63:    */ 
/*  64: 77 */     Point pMin = this.input.getXYCMinimum(0);
/*  65:    */     
/*  66:    */ 
/*  67: 80 */     int lMin = this.input.getXYByte(pMin.x, pMin.y);
/*  68: 81 */     this.queue.add(pMin, lMin);
/*  69:    */     
/*  70: 83 */     this.levroot[lMin] = pMin;
/*  71: 86 */     if (flood(lMin, pMin) != -1) {
/*  72: 86 */       throw new GlobalException("Flooding did not terminate with -1..something is wrong :(");
/*  73:    */     }
/*  74: 96 */     for (int y = 0; y < this.ydim; y++) {
/*  75: 97 */       for (int x = 0; x < this.xdim; x++)
/*  76:    */       {
/*  77: 98 */         int p = this.parent.getXYInt(x, y);
/*  78:    */         
/*  79:    */ 
/*  80:101 */         int _x = p % this.xdim;
/*  81:102 */         int _y = p / this.xdim;
/*  82:    */         
/*  83:    */ 
/*  84:105 */         Node n = this.nodes[y][x];
/*  85:    */         
/*  86:    */ 
/*  87:108 */         n.pixels.add(new Point(x, y));
/*  88:113 */         if ((y != _y) || (x != _x))
/*  89:    */         {
/*  90:115 */           n.parent = this.nodes[_y][_x];
/*  91:118 */           if (n.level > this.nodes[_y][_x].level) {
/*  92:119 */             this.nodes[_y][_x].children.add(n);
/*  93:    */           }
/*  94:    */         }
/*  95:    */       }
/*  96:    */     }
/*  97:124 */     this.nodes[((Point)this.S.get(this.S.size() - 1)).y][((Point)this.S.get(this.S.size() - 1)).x].parent = null;
/*  98:125 */     this.root = this.nodes[((Point)this.S.get(this.S.size() - 1)).y][((Point)this.S.get(this.S.size() - 1)).x];
/*  99:128 */     for (int i = this.S.size() - 1; i >= 0; i--)
/* 100:    */     {
/* 101:129 */       Point r = (Point)this.S.get(i);
/* 102:    */       
/* 103:131 */       Node p = this.nodes[r.y][r.x];
/* 104:132 */       Node q = p.parent;
/* 105:135 */       if ((q != null) && (q.level == p.level))
/* 106:    */       {
/* 107:137 */         q.pixels.addAll(p.pixels);
/* 108:138 */         q.children.addAll(p.children);
/* 109:    */         
/* 110:    */ 
/* 111:141 */         this.nodes[r.y][r.x] = null;
/* 112:    */       }
/* 113:    */     }
/* 114:147 */     for (int i = 0; i < this.S.size() - 1; i++)
/* 115:    */     {
/* 116:148 */       Point r = (Point)this.S.get(i);
/* 117:    */       
/* 118:150 */       Node p = this.nodes[r.y][r.x];
/* 119:152 */       if (p != null)
/* 120:    */       {
/* 121:154 */         Node q = p.parent;
/* 122:    */         
/* 123:    */ 
/* 124:    */ 
/* 125:158 */         q.pixels.addAll(p.pixels);
/* 126:    */       }
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   private int flood(int lambda, Point r)
/* 131:    */   {
/* 132:164 */     while (!this.queue.isEmpty(lambda))
/* 133:    */     {
/* 134:166 */       Point p = this.queue.get(lambda);
/* 135:    */       
/* 136:168 */       this.parent.setXYInt(p.x, p.y, r.y * this.xdim + r.x);
/* 137:170 */       if (this.nodes[p.y][p.x] == null) {
/* 138:170 */         this.nodes[p.y][p.x] = new Node(this.input.getXYByte(p.x, p.y));
/* 139:    */       }
/* 140:172 */       if (!p.equals(r)) {
/* 141:173 */         this.S.add(p);
/* 142:    */       }
/* 143:176 */       for (int i = 0; i < Tools.N.length; i++)
/* 144:    */       {
/* 145:177 */         int _x = p.x + Tools.N[i].x;
/* 146:178 */         int _y = p.y + Tools.N[i].y;
/* 147:181 */         if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 148:183 */           if (this.parent.getXYInt(_x, _y) == -1)
/* 149:    */           {
/* 150:185 */             Point n = new Point(_x, _y);
/* 151:    */             
/* 152:187 */             int l = this.input.getXYByte(n.x, n.y);
/* 153:189 */             if (this.levroot[l] == null) {
/* 154:189 */               this.levroot[l] = n;
/* 155:    */             }
/* 156:191 */             this.queue.add(n, l);
/* 157:    */             
/* 158:193 */             this.parent.setXYInt(n.x, n.y, -2);
/* 159:195 */             while (l > lambda) {
/* 160:196 */               l = flood(l, this.levroot[l]);
/* 161:    */             }
/* 162:    */           }
/* 163:    */         }
/* 164:    */       }
/* 165:    */     }
/* 166:201 */     this.levroot[lambda] = null;
/* 167:    */     
/* 168:203 */     int lpar = lambda - 1;
/* 169:205 */     while ((lpar >= 0) && (this.levroot[lpar] == null)) {
/* 170:206 */       lpar--;
/* 171:    */     }
/* 172:208 */     if (lpar != -1) {
/* 173:209 */       this.parent.setXYInt(r.x, r.y, this.levroot[lpar].y * this.xdim + this.levroot[lpar].x);
/* 174:    */     }
/* 175:211 */     this.S.add(r);
/* 176:213 */     if (this.nodes[r.y][r.x] == null) {
/* 177:213 */       this.nodes[r.y][r.x] = new Node(this.input.getXYByte(r.x, r.y));
/* 178:    */     }
/* 179:215 */     return lpar;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public static void printTree(Node root)
/* 183:    */   {
/* 184:220 */     if (root != null)
/* 185:    */     {
/* 186:221 */       System.err.println("I am pixel " + root.level);
/* 187:222 */       System.err.println("I have " + root.children.size() + " children");
/* 188:223 */       System.err.println("I have " + root.pixels.size() + " pixels");
/* 189:225 */       for (Node childNode : root.children) {
/* 190:226 */         printTree(childNode);
/* 191:    */       }
/* 192:    */     }
/* 193:    */   }
/* 194:    */   
/* 195:    */   public static Image tree2Image(Node root, int xdim, int ydim)
/* 196:    */   {
/* 197:232 */     ByteImage img = new ByteImage(xdim, ydim, 1);
/* 198:233 */     img.fill(0);
/* 199:    */     
/* 200:235 */     fillImage(img, root);
/* 201:    */     
/* 202:237 */     return img;
/* 203:    */   }
/* 204:    */   
/* 205:    */   private static void fillImage(Image img, Node node)
/* 206:    */   {
/* 207:242 */     if (node != null)
/* 208:    */     {
/* 209:244 */       for (Point p : node.pixels) {
/* 210:245 */         img.setXYByte(p.x, p.y, node.level);
/* 211:    */       }
/* 212:247 */       for (Node childNode : node.children) {
/* 213:248 */         fillImage(img, childNode);
/* 214:    */       }
/* 215:    */     }
/* 216:    */   }
/* 217:    */   
/* 218:    */   public static Node invoke(Image input)
/* 219:    */   {
/* 220:    */     try
/* 221:    */     {
/* 222:254 */       return (Node)new MaxTree().preprocess(new Object[] { input });
/* 223:    */     }
/* 224:    */     catch (GlobalException e)
/* 225:    */     {
/* 226:256 */       e.printStackTrace();
/* 227:    */     }
/* 228:257 */     return null;
/* 229:    */   }
/* 230:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.maxtree.MaxTree
 * JD-Core Version:    0.7.0.1
 */