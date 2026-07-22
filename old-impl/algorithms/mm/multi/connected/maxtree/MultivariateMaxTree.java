/*   1:    */ package vpt.algorithms.mm.multi.connected.maxtree;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.Stack;
/*   7:    */ import vpt.Algorithm;
/*   8:    */ import vpt.ByteImage;
/*   9:    */ import vpt.GlobalException;
/*  10:    */ import vpt.Image;
/*  11:    */ import vpt.IntegerImage;
/*  12:    */ import vpt.util.HierarchicalQueue;
/*  13:    */ import vpt.util.Tools;
/*  14:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  15:    */ 
/*  16:    */ public class MultivariateMaxTree
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19: 36 */   public Image input = null;
/*  20: 37 */   public MaxTreeNode root = null;
/*  21: 38 */   public AlgebraicOrdering vo = null;
/*  22: 41 */   private HierarchicalQueue queue = null;
/*  23: 42 */   private MaxTreeNode[][] nodes = null;
/*  24: 43 */   private ArrayList<Point> S = null;
/*  25: 44 */   private Point[] levroot = null;
/*  26: 45 */   private Image parent = null;
/*  27: 48 */   private final int NOT_ANALYZED = -1;
/*  28: 49 */   private final int IN_THE_QUEUE = -2;
/*  29:    */   private int xdim;
/*  30:    */   private int ydim;
/*  31: 55 */   public static Pixel[] ranks = null;
/*  32: 56 */   private IntegerImage rankImage = null;
/*  33: 57 */   private ArrayList<Integer> actualRanks = null;
/*  34: 58 */   public static Integer[] actualRanksArray = null;
/*  35:    */   
/*  36:    */   public MultivariateMaxTree()
/*  37:    */   {
/*  38: 61 */     this.inputFields = "input, vo";
/*  39: 62 */     this.outputFields = "root";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void execute()
/*  43:    */     throws GlobalException
/*  44:    */   {
/*  45: 67 */     if (this.input.getCDim() < 2) {
/*  46: 67 */       throw new GlobalException("It needs at an image with at least two bands");
/*  47:    */     }
/*  48: 69 */     this.xdim = this.input.getXDim();
/*  49: 70 */     this.ydim = this.input.getYDim();
/*  50:    */     
/*  51:    */ 
/*  52: 73 */     this.rankImage = new IntegerImage(this.xdim, this.ydim, 1);
/*  53:    */     
/*  54:    */ 
/*  55: 76 */     ranks = new Pixel[this.xdim * this.ydim];
/*  56: 78 */     for (int y = 0; y < this.ydim; y++) {
/*  57: 79 */       for (int x = 0; x < this.xdim; x++) {
/*  58: 80 */         ranks[(y * this.xdim + x)] = new Pixel(x, y, this.input.getVXYDouble(x, y));
/*  59:    */       }
/*  60:    */     }
/*  61: 85 */     Arrays.sort(ranks);
/*  62:    */     
/*  63:    */ 
/*  64:    */ 
/*  65: 89 */     Pixel lastSeenVector = null;
/*  66:    */     
/*  67: 91 */     this.actualRanks = new ArrayList();
/*  68: 94 */     for (int i = 0; i < ranks.length; i++)
/*  69:    */     {
/*  70: 95 */       Pixel r = ranks[i];
/*  71: 97 */       if ((lastSeenVector == null) || (!r.equals(lastSeenVector)))
/*  72:    */       {
/*  73: 98 */         lastSeenVector = r;
/*  74: 99 */         this.actualRanks.add(Integer.valueOf(i));
/*  75:    */       }
/*  76:103 */       this.rankImage.setXYInt(r.x, r.y, this.actualRanks.size() - 1);
/*  77:    */     }
/*  78:107 */     actualRanksArray = new Integer[this.actualRanks.size()];
/*  79:    */     
/*  80:    */ 
/*  81:110 */     int cnt = 0;
/*  82:111 */     for (Integer i : this.actualRanks) {
/*  83:112 */       actualRanksArray[(cnt++)] = i;
/*  84:    */     }
/*  85:117 */     this.levroot = new Point[actualRanksArray.length];
/*  86:118 */     Arrays.fill(this.levroot, null);
/*  87:    */     
/*  88:120 */     this.parent = new IntegerImage(this.xdim, this.ydim, 1);
/*  89:121 */     this.parent.fill(-1);
/*  90:    */     
/*  91:    */ 
/*  92:124 */     this.S = new ArrayList();
/*  93:    */     
/*  94:    */ 
/*  95:    */ 
/*  96:128 */     this.nodes = new MaxTreeNode[this.ydim][this.xdim];
/*  97:    */     
/*  98:    */ 
/*  99:131 */     this.queue = new HierarchicalQueue(actualRanksArray.length);
/* 100:    */     
/* 101:    */ 
/* 102:134 */     Point pMin = this.rankImage.getXYCMinimum(0);
/* 103:    */     
/* 104:    */ 
/* 105:137 */     int lMin = this.rankImage.getXYInt(pMin.x, pMin.y);
/* 106:138 */     this.queue.add(pMin, lMin);
/* 107:    */     
/* 108:140 */     this.levroot[lMin] = pMin;
/* 109:143 */     if (flood(lMin, pMin) != -1) {
/* 110:143 */       throw new GlobalException("Flooding did not terminate with -1..something is wrong :(");
/* 111:    */     }
/* 112:147 */     for (int y = 0; y < this.ydim; y++) {
/* 113:148 */       for (int x = 0; x < this.xdim; x++)
/* 114:    */       {
/* 115:149 */         int p = this.parent.getXYInt(x, y);
/* 116:    */         
/* 117:    */ 
/* 118:152 */         int _x = p % this.xdim;
/* 119:153 */         int _y = p / this.xdim;
/* 120:    */         
/* 121:    */ 
/* 122:156 */         MaxTreeNode n = this.nodes[y][x];
/* 123:    */         
/* 124:    */ 
/* 125:159 */         n.pixels.addFirst(new Point(x, y));
/* 126:164 */         if ((y != _y) || (x != _x))
/* 127:    */         {
/* 128:166 */           n.parent = this.nodes[_y][_x];
/* 129:169 */           if (n.level > this.nodes[_y][_x].level) {
/* 130:170 */             this.nodes[_y][_x].children.addFirst(n);
/* 131:    */           }
/* 132:    */         }
/* 133:    */       }
/* 134:    */     }
/* 135:175 */     this.nodes[((Point)this.S.get(this.S.size() - 1)).y][((Point)this.S.get(this.S.size() - 1)).x].parent = null;
/* 136:176 */     this.root = this.nodes[((Point)this.S.get(this.S.size() - 1)).y][((Point)this.S.get(this.S.size() - 1)).x];
/* 137:179 */     for (int i = this.S.size() - 1; i >= 0; i--)
/* 138:    */     {
/* 139:180 */       Point r = (Point)this.S.get(i);
/* 140:    */       
/* 141:182 */       MaxTreeNode p = this.nodes[r.y][r.x];
/* 142:183 */       MaxTreeNode q = p.parent;
/* 143:186 */       if ((q != null) && (q.level == p.level))
/* 144:    */       {
/* 145:188 */         q.pixels.addAll(p.pixels);
/* 146:189 */         q.children.addAll(p.children);
/* 147:    */         
/* 148:    */ 
/* 149:192 */         this.nodes[r.y][r.x] = null;
/* 150:    */       }
/* 151:    */     }
/* 152:    */   }
/* 153:    */   
/* 154:    */   private int flood(int lambda, Point r)
/* 155:    */   {
/* 156:216 */     while (!this.queue.isEmpty(lambda))
/* 157:    */     {
/* 158:219 */       Point p = this.queue.get(lambda);
/* 159:    */       
/* 160:    */ 
/* 161:222 */       this.parent.setXYInt(p.x, p.y, r.y * this.xdim + r.x);
/* 162:224 */       if (this.nodes[p.y][p.x] == null) {
/* 163:224 */         this.nodes[p.y][p.x] = new MaxTreeNode(this.rankImage.getXYInt(p.x, p.y));
/* 164:    */       }
/* 165:226 */       if (!p.equals(r)) {
/* 166:227 */         this.S.add(p);
/* 167:    */       }
/* 168:230 */       for (int i = 0; i < Tools.N.length; i++)
/* 169:    */       {
/* 170:231 */         int _x = p.x + Tools.N[i].x;
/* 171:232 */         int _y = p.y + Tools.N[i].y;
/* 172:235 */         if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 173:237 */           if (this.parent.getXYInt(_x, _y) == -1)
/* 174:    */           {
/* 175:239 */             Point n = new Point(_x, _y);
/* 176:    */             
/* 177:241 */             int l = this.rankImage.getXYInt(n.x, n.y);
/* 178:243 */             if (this.levroot[l] == null) {
/* 179:243 */               this.levroot[l] = n;
/* 180:    */             }
/* 181:245 */             this.queue.add(n, l);
/* 182:    */             
/* 183:247 */             this.parent.setXYInt(n.x, n.y, -2);
/* 184:249 */             while (l > lambda) {
/* 185:250 */               l = flood(l, this.levroot[l]);
/* 186:    */             }
/* 187:    */           }
/* 188:    */         }
/* 189:    */       }
/* 190:    */     }
/* 191:255 */     this.levroot[lambda] = null;
/* 192:    */     
/* 193:257 */     int lpar = lambda - 1;
/* 194:259 */     while ((lpar >= 0) && (this.levroot[lpar] == null)) {
/* 195:260 */       lpar--;
/* 196:    */     }
/* 197:262 */     if (lpar != -1) {
/* 198:263 */       this.parent.setXYInt(r.x, r.y, this.levroot[lpar].y * this.xdim + this.levroot[lpar].x);
/* 199:    */     }
/* 200:265 */     this.S.add(r);
/* 201:267 */     if (this.nodes[r.y][r.x] == null) {
/* 202:267 */       this.nodes[r.y][r.x] = new MaxTreeNode(this.rankImage.getXYInt(r.x, r.y));
/* 203:    */     }
/* 204:269 */     return lpar;
/* 205:    */   }
/* 206:    */   
/* 207:    */   public static Image tree2Image(MaxTreeNode root, int xdim, int ydim, int cdim)
/* 208:    */   {
/* 209:286 */     ByteImage img = new ByteImage(xdim, ydim, cdim);
/* 210:287 */     img.fill(0);
/* 211:    */     
/* 212:289 */     Stack<MaxTreeNode> st = new Stack();
/* 213:290 */     st.push(root);
/* 214:    */     
/* 215:292 */     fillImage(img, st);
/* 216:    */     
/* 217:294 */     return img;
/* 218:    */   }
/* 219:    */   
/* 220:    */   private static void fillImage(Image img, Stack<MaxTreeNode> st)
/* 221:    */   {
/* 222:308 */     while (!st.isEmpty())
/* 223:    */     {
/* 224:310 */       MaxTreeNode node = (MaxTreeNode)st.pop();
/* 225:314 */       for (MyListNode<Point> n = node.pixels.getHead(); n != null; n = n.next)
/* 226:    */       {
/* 227:315 */         Point p = (Point)n.datum;
/* 228:316 */         img.setVXYDouble(p.x, p.y, ranks[actualRanksArray[node.level].intValue()].val);
/* 229:    */       }
/* 230:320 */       for (MyListNode<MaxTreeNode> n = node.children.getHead(); n != null; n = n.next)
/* 231:    */       {
/* 232:321 */         MaxTreeNode tmp = (MaxTreeNode)n.datum;
/* 233:322 */         st.push(tmp);
/* 234:    */       }
/* 235:    */     }
/* 236:    */   }
/* 237:    */   
/* 238:    */   private class Pixel
/* 239:    */     implements Comparable<Pixel>
/* 240:    */   {
/* 241:    */     int x;
/* 242:    */     int y;
/* 243:    */     double[] val;
/* 244:    */     
/* 245:    */     Pixel(int x, int y, double[] val)
/* 246:    */     {
/* 247:354 */       this.x = x;
/* 248:355 */       this.y = y;
/* 249:356 */       this.val = val;
/* 250:    */     }
/* 251:    */     
/* 252:    */     public boolean equals(Object o)
/* 253:    */     {
/* 254:361 */       Pixel p = (Pixel)o;
/* 255:    */       
/* 256:363 */       boolean equal = true;
/* 257:365 */       for (int i = 0; i < this.val.length; i++) {
/* 258:366 */         if (Tools.cmpr(p.val[i], this.val[i]) != 0) {
/* 259:366 */           equal = false;
/* 260:    */         }
/* 261:    */       }
/* 262:368 */       return equal;
/* 263:    */     }
/* 264:    */     
/* 265:    */     public int compareTo(Pixel c)
/* 266:    */     {
/* 267:372 */       return MultivariateMaxTree.this.vo.compare(this.val, c.val);
/* 268:    */     }
/* 269:    */   }
/* 270:    */   
/* 271:    */   public static MaxTreeNode invoke(Image input, AlgebraicOrdering vo)
/* 272:    */   {
/* 273:    */     try
/* 274:    */     {
/* 275:378 */       return (MaxTreeNode)new MultivariateMaxTree().preprocess(new Object[] { input, vo });
/* 276:    */     }
/* 277:    */     catch (GlobalException e)
/* 278:    */     {
/* 279:380 */       e.printStackTrace();
/* 280:    */     }
/* 281:381 */     return null;
/* 282:    */   }
/* 283:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.maxtree.MultivariateMaxTree
 * JD-Core Version:    0.7.0.1
 */