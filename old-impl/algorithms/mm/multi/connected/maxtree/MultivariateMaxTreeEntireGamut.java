/*   1:    */ package vpt.algorithms.mm.multi.connected.maxtree;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.Stack;
/*   8:    */ import vpt.Algorithm;
/*   9:    */ import vpt.ByteImage;
/*  10:    */ import vpt.GlobalException;
/*  11:    */ import vpt.Image;
/*  12:    */ import vpt.IntegerImage;
/*  13:    */ import vpt.util.HierarchicalQueue;
/*  14:    */ import vpt.util.Tools;
/*  15:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  16:    */ 
/*  17:    */ public class MultivariateMaxTreeEntireGamut
/*  18:    */   extends Algorithm
/*  19:    */ {
/*  20: 36 */   public Image input = null;
/*  21: 37 */   public MaxTreeNode root = null;
/*  22: 38 */   public AlgebraicOrdering vo = null;
/*  23: 41 */   private HierarchicalQueue queue = null;
/*  24: 42 */   private MaxTreeNode[][] nodes = null;
/*  25: 43 */   private ArrayList<Point> S = null;
/*  26: 44 */   private Point[] levroot = null;
/*  27: 45 */   private Image parent = null;
/*  28: 48 */   private final int NOT_ANALYZED = -1;
/*  29: 49 */   private final int IN_THE_QUEUE = -2;
/*  30:    */   private int xdim;
/*  31:    */   private int ydim;
/*  32: 55 */   public static ColorVector[] cv = null;
/*  33: 56 */   private IntegerImage rankImage = null;
/*  34: 57 */   private ArrayList<Integer> actualRanks = null;
/*  35: 58 */   public static Integer[] actualRanksArray = null;
/*  36:    */   
/*  37:    */   public MultivariateMaxTreeEntireGamut()
/*  38:    */   {
/*  39: 61 */     this.inputFields = "input, vo";
/*  40: 62 */     this.outputFields = "root";
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void execute()
/*  44:    */     throws GlobalException
/*  45:    */   {
/*  46: 67 */     if (this.input.getCDim() < 2) {
/*  47: 67 */       throw new GlobalException("It needs at an image with at least two bands");
/*  48:    */     }
/*  49: 69 */     this.xdim = this.input.getXDim();
/*  50: 70 */     this.ydim = this.input.getYDim();
/*  51:    */     
/*  52:    */ 
/*  53: 73 */     initColorVectors();
/*  54:    */     
/*  55:    */ 
/*  56: 76 */     this.rankImage = new IntegerImage(this.xdim, this.ydim, 1);
/*  57: 79 */     for (int y = 0; y < this.ydim; y++) {
/*  58: 80 */       for (int x = 0; x < this.xdim; x++) {
/*  59: 81 */         this.rankImage.setXYInt(x, y, rankOf(this.input.getVXYDouble(x, y)));
/*  60:    */       }
/*  61:    */     }
/*  62: 87 */     Pixel[] ranks = new Pixel[this.xdim * this.ydim];
/*  63: 89 */     for (int y = 0; y < this.ydim; y++) {
/*  64: 90 */       for (int x = 0; x < this.xdim; x++) {
/*  65: 91 */         ranks[(y * this.xdim + x)] = new Pixel(x, y, this.rankImage.getXYInt(x, y));
/*  66:    */       }
/*  67:    */     }
/*  68: 93 */     Arrays.sort(ranks);
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72: 97 */     this.actualRanks = new ArrayList();
/*  73:    */     
/*  74: 99 */     int lastSeenRank = -1;
/*  75:101 */     for (int i = 0; i < ranks.length; i++)
/*  76:    */     {
/*  77:102 */       Pixel r = ranks[i];
/*  78:104 */       if (r.val != lastSeenRank)
/*  79:    */       {
/*  80:105 */         lastSeenRank = r.val;
/*  81:106 */         this.actualRanks.add(Integer.valueOf(r.val));
/*  82:    */       }
/*  83:109 */       this.rankImage.setXYInt(r.x, r.y, this.actualRanks.size() - 1);
/*  84:    */     }
/*  85:112 */     actualRanksArray = new Integer[this.actualRanks.size()];
/*  86:    */     
/*  87:    */ 
/*  88:115 */     int cnt = 0;
/*  89:116 */     for (Integer i : this.actualRanks) {
/*  90:117 */       actualRanksArray[(cnt++)] = i;
/*  91:    */     }
/*  92:122 */     this.levroot = new Point[actualRanksArray.length];
/*  93:123 */     Arrays.fill(this.levroot, null);
/*  94:    */     
/*  95:125 */     this.parent = new IntegerImage(this.xdim, this.ydim, 1);
/*  96:126 */     this.parent.fill(-1);
/*  97:    */     
/*  98:    */ 
/*  99:129 */     this.S = new ArrayList();
/* 100:    */     
/* 101:    */ 
/* 102:    */ 
/* 103:133 */     this.nodes = new MaxTreeNode[this.ydim][this.xdim];
/* 104:    */     
/* 105:    */ 
/* 106:136 */     this.queue = new HierarchicalQueue(actualRanksArray.length);
/* 107:    */     
/* 108:    */ 
/* 109:139 */     Point pMin = this.rankImage.getXYCMinimum(0);
/* 110:    */     
/* 111:    */ 
/* 112:142 */     int lMin = this.rankImage.getXYInt(pMin.x, pMin.y);
/* 113:143 */     this.queue.add(pMin, lMin);
/* 114:    */     
/* 115:145 */     this.levroot[lMin] = pMin;
/* 116:148 */     if (flood(lMin, pMin) != -1) {
/* 117:148 */       throw new GlobalException("Flooding did not terminate with -1..something is wrong :(");
/* 118:    */     }
/* 119:152 */     for (int y = 0; y < this.ydim; y++) {
/* 120:153 */       for (int x = 0; x < this.xdim; x++)
/* 121:    */       {
/* 122:154 */         int p = this.parent.getXYInt(x, y);
/* 123:    */         
/* 124:    */ 
/* 125:157 */         int _x = p % this.xdim;
/* 126:158 */         int _y = p / this.xdim;
/* 127:    */         
/* 128:    */ 
/* 129:161 */         MaxTreeNode n = this.nodes[y][x];
/* 130:    */         
/* 131:    */ 
/* 132:164 */         n.pixels.addFirst(new Point(x, y));
/* 133:169 */         if ((y != _y) || (x != _x))
/* 134:    */         {
/* 135:171 */           n.parent = this.nodes[_y][_x];
/* 136:174 */           if (n.level > this.nodes[_y][_x].level) {
/* 137:175 */             this.nodes[_y][_x].children.addFirst(n);
/* 138:    */           }
/* 139:    */         }
/* 140:    */       }
/* 141:    */     }
/* 142:180 */     this.nodes[((Point)this.S.get(this.S.size() - 1)).y][((Point)this.S.get(this.S.size() - 1)).x].parent = null;
/* 143:181 */     this.root = this.nodes[((Point)this.S.get(this.S.size() - 1)).y][((Point)this.S.get(this.S.size() - 1)).x];
/* 144:184 */     for (int i = this.S.size() - 1; i >= 0; i--)
/* 145:    */     {
/* 146:185 */       Point r = (Point)this.S.get(i);
/* 147:    */       
/* 148:187 */       MaxTreeNode p = this.nodes[r.y][r.x];
/* 149:188 */       MaxTreeNode q = p.parent;
/* 150:191 */       if ((q != null) && (q.level == p.level))
/* 151:    */       {
/* 152:193 */         q.pixels.addAll(p.pixels);
/* 153:194 */         q.children.addAll(p.children);
/* 154:    */         
/* 155:    */ 
/* 156:197 */         this.nodes[r.y][r.x] = null;
/* 157:    */       }
/* 158:    */     }
/* 159:    */   }
/* 160:    */   
/* 161:    */   private int flood(int lambda, Point r)
/* 162:    */   {
/* 163:221 */     while (!this.queue.isEmpty(lambda))
/* 164:    */     {
/* 165:224 */       Point p = this.queue.get(lambda);
/* 166:    */       
/* 167:    */ 
/* 168:227 */       this.parent.setXYInt(p.x, p.y, r.y * this.xdim + r.x);
/* 169:229 */       if (this.nodes[p.y][p.x] == null) {
/* 170:229 */         this.nodes[p.y][p.x] = new MaxTreeNode(this.rankImage.getXYInt(p.x, p.y));
/* 171:    */       }
/* 172:231 */       if (!p.equals(r)) {
/* 173:232 */         this.S.add(p);
/* 174:    */       }
/* 175:235 */       for (int i = 0; i < Tools.N.length; i++)
/* 176:    */       {
/* 177:236 */         int _x = p.x + Tools.N[i].x;
/* 178:237 */         int _y = p.y + Tools.N[i].y;
/* 179:240 */         if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 180:242 */           if (this.parent.getXYInt(_x, _y) == -1)
/* 181:    */           {
/* 182:244 */             Point n = new Point(_x, _y);
/* 183:    */             
/* 184:246 */             int l = this.rankImage.getXYInt(n.x, n.y);
/* 185:248 */             if (this.levroot[l] == null) {
/* 186:248 */               this.levroot[l] = n;
/* 187:    */             }
/* 188:250 */             this.queue.add(n, l);
/* 189:    */             
/* 190:252 */             this.parent.setXYInt(n.x, n.y, -2);
/* 191:254 */             while (l > lambda) {
/* 192:255 */               l = flood(l, this.levroot[l]);
/* 193:    */             }
/* 194:    */           }
/* 195:    */         }
/* 196:    */       }
/* 197:    */     }
/* 198:260 */     this.levroot[lambda] = null;
/* 199:    */     
/* 200:262 */     int lpar = lambda - 1;
/* 201:264 */     while ((lpar >= 0) && (this.levroot[lpar] == null)) {
/* 202:265 */       lpar--;
/* 203:    */     }
/* 204:267 */     if (lpar != -1) {
/* 205:268 */       this.parent.setXYInt(r.x, r.y, this.levroot[lpar].y * this.xdim + this.levroot[lpar].x);
/* 206:    */     }
/* 207:270 */     this.S.add(r);
/* 208:272 */     if (this.nodes[r.y][r.x] == null) {
/* 209:272 */       this.nodes[r.y][r.x] = new MaxTreeNode(this.rankImage.getXYInt(r.x, r.y));
/* 210:    */     }
/* 211:274 */     return lpar;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public static Image tree2Image(MaxTreeNode root, int xdim, int ydim, int cdim)
/* 215:    */   {
/* 216:291 */     ByteImage img = new ByteImage(xdim, ydim, cdim);
/* 217:292 */     img.fill(0);
/* 218:    */     
/* 219:294 */     Stack<MaxTreeNode> st = new Stack();
/* 220:295 */     st.push(root);
/* 221:    */     
/* 222:297 */     fillImage(img, st);
/* 223:    */     
/* 224:299 */     return img;
/* 225:    */   }
/* 226:    */   
/* 227:    */   private static void fillImage(Image img, Stack<MaxTreeNode> st)
/* 228:    */   {
/* 229:313 */     while (!st.isEmpty())
/* 230:    */     {
/* 231:315 */       MaxTreeNode node = (MaxTreeNode)st.pop();
/* 232:319 */       for (MyListNode<Point> n = node.pixels.getHead(); n != null; n = n.next)
/* 233:    */       {
/* 234:320 */         Point p = (Point)n.datum;
/* 235:321 */         img.setVXYDouble(p.x, p.y, cv[actualRanksArray[node.level].intValue()].z);
/* 236:    */       }
/* 237:325 */       for (MyListNode<MaxTreeNode> n = node.children.getHead(); n != null; n = n.next)
/* 238:    */       {
/* 239:326 */         MaxTreeNode tmp = (MaxTreeNode)n.datum;
/* 240:327 */         st.push(tmp);
/* 241:    */       }
/* 242:    */     }
/* 243:    */   }
/* 244:    */   
/* 245:    */   private void initColorVectors()
/* 246:    */   {
/* 247:354 */     cv = new ColorVector[16777216];
/* 248:    */     
/* 249:356 */     int j = 0;
/* 250:357 */     for (int r = 0; r < 256; r++) {
/* 251:358 */       for (int g = 0; g < 256; g++) {
/* 252:359 */         for (int b = 0; b < 256; b++) {
/* 253:360 */           cv[(j++)] = new ColorVector(r, g, b);
/* 254:    */         }
/* 255:    */       }
/* 256:    */     }
/* 257:364 */     System.err.println("Sorting colors");
/* 258:    */     
/* 259:366 */     Arrays.sort(cv);
/* 260:    */     
/* 261:368 */     System.err.println("Colors sorted");
/* 262:    */   }
/* 263:    */   
/* 264:    */   private int rankOf(double[] a)
/* 265:    */   {
/* 266:372 */     int first = 0;
/* 267:373 */     int last = cv.length - 1;
/* 268:374 */     int mid = 0;
/* 269:    */     
/* 270:376 */     boolean found = false;
/* 271:378 */     while ((!found) && (first <= last))
/* 272:    */     {
/* 273:379 */       mid = first + (last - first) / 2;
/* 274:380 */       if (this.vo.compare(a, cv[mid].z) == 0) {
/* 275:380 */         found = true;
/* 276:381 */       } else if (this.vo.compare(a, cv[mid].z) < 0) {
/* 277:381 */         last = mid - 1;
/* 278:382 */       } else if (this.vo.compare(a, cv[mid].z) > 0) {
/* 279:382 */         first = mid + 1;
/* 280:    */       }
/* 281:    */     }
/* 282:385 */     return mid;
/* 283:    */   }
/* 284:    */   
/* 285:    */   private class Pixel
/* 286:    */     implements Comparable<Pixel>
/* 287:    */   {
/* 288:    */     int x;
/* 289:    */     int y;
/* 290:    */     int val;
/* 291:    */     
/* 292:    */     Pixel(int x, int y, int val)
/* 293:    */     {
/* 294:395 */       this.x = x;
/* 295:396 */       this.y = y;
/* 296:397 */       this.val = val;
/* 297:    */     }
/* 298:    */     
/* 299:    */     public int compareTo(Pixel o)
/* 300:    */     {
/* 301:401 */       if (this.val > o.val) {
/* 302:401 */         return 1;
/* 303:    */       }
/* 304:402 */       if (this.val < o.val) {
/* 305:402 */         return -1;
/* 306:    */       }
/* 307:403 */       return 0;
/* 308:    */     }
/* 309:    */   }
/* 310:    */   
/* 311:    */   private class ColorVector
/* 312:    */     implements Comparable<ColorVector>
/* 313:    */   {
/* 314:    */     double[] z;
/* 315:    */     
/* 316:    */     ColorVector(int a, int b, int c)
/* 317:    */     {
/* 318:412 */       this.z = new double[3];
/* 319:413 */       this.z[0] = (a * 0.00392156862745098D);
/* 320:414 */       this.z[1] = (b * 0.00392156862745098D);
/* 321:415 */       this.z[2] = (c * 0.00392156862745098D);
/* 322:    */     }
/* 323:    */     
/* 324:    */     public int compareTo(ColorVector c)
/* 325:    */     {
/* 326:419 */       return MultivariateMaxTreeEntireGamut.this.vo.compare(this.z, c.z);
/* 327:    */     }
/* 328:    */   }
/* 329:    */   
/* 330:    */   public static MaxTreeNode invoke(Image input, AlgebraicOrdering vo)
/* 331:    */   {
/* 332:    */     try
/* 333:    */     {
/* 334:425 */       return (MaxTreeNode)new MultivariateMaxTreeEntireGamut().preprocess(new Object[] { input, vo });
/* 335:    */     }
/* 336:    */     catch (GlobalException e)
/* 337:    */     {
/* 338:427 */       e.printStackTrace();
/* 339:    */     }
/* 340:428 */     return null;
/* 341:    */   }
/* 342:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.maxtree.MultivariateMaxTreeEntireGamut
 * JD-Core Version:    0.7.0.1
 */