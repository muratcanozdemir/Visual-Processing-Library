/*   1:    */ package vpt.algorithms.flatzones.color;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Collection;
/*   7:    */ import java.util.Iterator;
/*   8:    */ import java.util.Stack;
/*   9:    */ import java.util.TreeMap;
/*  10:    */ import vpt.Algorithm;
/*  11:    */ import vpt.ByteImage;
/*  12:    */ import vpt.GlobalException;
/*  13:    */ import vpt.Image;
/*  14:    */ import vpt.IntegerImage;
/*  15:    */ 
/*  16:    */ public class ColorQFZSoille
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19:    */   public Image input;
/*  20:    */   public IntegerImage output;
/*  21:    */   public Integer alpha;
/*  22:    */   public Integer omega;
/*  23: 35 */   public Point[] neighbors4 = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1) };
/*  24: 36 */   public Point[] neighbors8 = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  25:    */   
/*  26:    */   public ColorQFZSoille()
/*  27:    */   {
/*  28: 39 */     this.inputFields = "input,alpha,omega";
/*  29: 40 */     this.outputFields = "output";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void execute()
/*  33:    */     throws GlobalException
/*  34:    */   {
/*  35: 45 */     int xdim = this.input.getXDim();
/*  36: 46 */     int ydim = this.input.getYDim();
/*  37:    */     
/*  38:    */ 
/*  39: 49 */     Point[] N = this.neighbors8;
/*  40: 51 */     if (this.input.getCDim() != 3) {
/*  41: 51 */       throw new GlobalException("This implementation is only for color images.");
/*  42:    */     }
/*  43: 53 */     if ((this.alpha.intValue() < 0) || (this.alpha.intValue() > 254) || (this.omega.intValue() < 0) || (this.omega.intValue() > 254)) {
/*  44: 53 */       throw new GlobalException("Arguments out of range.");
/*  45:    */     }
/*  46: 56 */     IntegerImage lbl = new IntegerImage(xdim, ydim, 1);
/*  47: 57 */     lbl.fill(0);
/*  48:    */     
/*  49:    */ 
/*  50: 60 */     ByteImage rl = new ByteImage(xdim, ydim, 1);
/*  51: 61 */     rl.fill(255);
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56: 66 */     int[] mincc = new int[3];
/*  57: 67 */     int[] maxcc = new int[3];
/*  58: 68 */     int[] rlval = new int[3];
/*  59: 69 */     int[] diff = new int[3];
/*  60:    */     
/*  61:    */ 
/*  62: 72 */     Stack<Point> stack = new Stack();
/*  63:    */     
/*  64:    */ 
/*  65:    */ 
/*  66: 76 */     TreeMap<Integer, ArrayList<Point>> pq = new TreeMap();
/*  67:    */     
/*  68: 78 */     int lblval = 1;
/*  69: 81 */     for (int x = 0; x < xdim; x++) {
/*  70: 84 */       for (int y = 0; y < ydim; y++) {
/*  71: 87 */         if (lbl.getXYInt(x, y) == 0)
/*  72:    */         {
/*  73: 88 */           lbl.setXYInt(x, y, lblval);
/*  74:    */           
/*  75: 90 */           int[] fp = this.input.getVXYByte(x, y);
/*  76:    */           
/*  77: 92 */           maxcc[0] = fp[0];
/*  78: 93 */           maxcc[1] = fp[1];
/*  79: 94 */           maxcc[2] = fp[2];
/*  80:    */           
/*  81: 96 */           mincc[0] = fp[0];
/*  82: 97 */           mincc[1] = fp[1];
/*  83: 98 */           mincc[2] = fp[2];
/*  84:    */           
/*  85:100 */           int rlcrt = this.alpha.intValue();
/*  86:103 */           for (int i = 0; i < N.length; i++)
/*  87:    */           {
/*  88:105 */             int qx = x + N[i].x;
/*  89:106 */             int qy = y + N[i].y;
/*  90:109 */             if ((qx >= 0) && (qx < xdim) && (qy >= 0) && (qy < ydim))
/*  91:    */             {
/*  92:112 */               int[] fq = this.input.getVXYByte(qx, qy);
/*  93:    */               
/*  94:114 */               rlval[0] = Math.abs(fp[0] - fq[0]);
/*  95:115 */               rlval[1] = Math.abs(fp[1] - fq[1]);
/*  96:116 */               rlval[2] = Math.abs(fp[2] - fq[2]);
/*  97:118 */               if (lbl.getXYInt(qx, qy) > 0)
/*  98:    */               {
/*  99:119 */                 if ((rlcrt >= rlval[0]) && (rlcrt >= rlval[1]) && (rlcrt >= rlval[2])) {
/* 100:120 */                   rlcrt = maxDim(rlval) - 1;
/* 101:    */                 }
/* 102:    */               }
/* 103:125 */               else if ((rlval[0] <= rlcrt) && (rlval[1] <= rlcrt) && (rlval[2] <= rlcrt))
/* 104:    */               {
/* 105:126 */                 rl.setXYByte(qx, qy, maxDim(rlval));
/* 106:128 */                 if (pq.containsKey(Integer.valueOf(maxDim(rlval))))
/* 107:    */                 {
/* 108:129 */                   ((ArrayList)pq.get(Integer.valueOf(maxDim(rlval)))).add(new Point(qx, qy));
/* 109:    */                 }
/* 110:    */                 else
/* 111:    */                 {
/* 112:131 */                   ArrayList<Point> tmp = new ArrayList();
/* 113:132 */                   tmp.add(new Point(qx, qy));
/* 114:133 */                   pq.put(Integer.valueOf(maxDim(rlval)), tmp);
/* 115:    */                 }
/* 116:    */               }
/* 117:    */             }
/* 118:    */           }
/* 119:    */           int rcrt;
/* 120:    */           int rcrt;
/* 121:139 */           if (!pq.isEmpty()) {
/* 122:140 */             rcrt = ((Integer)pq.firstKey()).intValue();
/* 123:    */           } else {
/* 124:142 */             rcrt = 0;
/* 125:    */           }
/* 126:144 */           while (!pq.isEmpty())
/* 127:    */           {
/* 128:145 */             int datumPrio = ((Integer)pq.firstKey()).intValue();
/* 129:146 */             ArrayList<Point> tmp = (ArrayList)pq.get(Integer.valueOf(datumPrio));
/* 130:147 */             Point datumP = (Point)tmp.remove(0);
/* 131:150 */             if (tmp.isEmpty()) {
/* 132:151 */               pq.pollFirstEntry();
/* 133:    */             }
/* 134:153 */             if (lbl.getXYInt(datumP.x, datumP.y) <= 0) {
/* 135:156 */               if (datumPrio > rcrt)
/* 136:    */               {
/* 137:157 */                 while (!stack.isEmpty())
/* 138:    */                 {
/* 139:158 */                   Point sPoint = (Point)stack.pop();
/* 140:159 */                   lbl.setXYInt(sPoint.x, sPoint.y, lblval);
/* 141:    */                 }
/* 142:162 */                 rcrt = datumPrio;
/* 143:164 */                 if (lbl.getXYInt(datumP.x, datumP.y) > 0) {}
/* 144:    */               }
/* 145:    */               else
/* 146:    */               {
/* 147:168 */                 stack.add(datumP);
/* 148:    */                 
/* 149:170 */                 int[] fDatumP = this.input.getVXYByte(datumP.x, datumP.y);
/* 150:    */                 
/* 151:172 */                 mincc[0] = (mincc[0] > fDatumP[0] ? fDatumP[0] : mincc[0]);
/* 152:173 */                 mincc[1] = (mincc[1] > fDatumP[1] ? fDatumP[1] : mincc[1]);
/* 153:174 */                 mincc[2] = (mincc[2] > fDatumP[2] ? fDatumP[2] : mincc[2]);
/* 154:    */                 
/* 155:176 */                 maxcc[0] = (maxcc[0] < fDatumP[0] ? fDatumP[0] : maxcc[0]);
/* 156:177 */                 maxcc[1] = (maxcc[1] < fDatumP[1] ? fDatumP[1] : maxcc[1]);
/* 157:178 */                 maxcc[2] = (maxcc[2] < fDatumP[2] ? fDatumP[2] : maxcc[2]);
/* 158:    */                 
/* 159:180 */                 maxcc[0] -= mincc[0];
/* 160:181 */                 maxcc[1] -= mincc[1];
/* 161:182 */                 maxcc[2] -= mincc[2];
/* 162:184 */                 if ((this.omega.intValue() < diff[0]) || (this.omega.intValue() < diff[1]) || (this.omega.intValue() < diff[2]) || (rcrt > rlcrt))
/* 163:    */                 {
/* 164:186 */                   for (Point pp : stack) {
/* 165:187 */                     rl.setXYByte(pp.x, pp.y, 255);
/* 166:    */                   }
/* 167:189 */                   stack.clear();
/* 168:    */                   
/* 169:191 */                   Collection<ArrayList<Point>> pointLists = pq.values();
/* 170:    */                   Iterator localIterator3;
/* 171:193 */                   for (Iterator localIterator2 = pointLists.iterator(); localIterator2.hasNext(); localIterator3.hasNext())
/* 172:    */                   {
/* 173:193 */                     Object pointList = (ArrayList)localIterator2.next();
/* 174:    */                     
/* 175:195 */                     localIterator3 = ((ArrayList)pointList).iterator(); continue;Point p = (Point)localIterator3.next();
/* 176:196 */                     rl.setXYByte(p.x, p.y, 255);
/* 177:    */                   }
/* 178:199 */                   pq.clear();
/* 179:200 */                   break;
/* 180:    */                 }
/* 181:203 */                 for (int i = 0; i < N.length; i++)
/* 182:    */                 {
/* 183:205 */                   int qx = datumP.x + N[i].x;
/* 184:206 */                   int qy = datumP.y + N[i].y;
/* 185:209 */                   if ((qx >= 0) && (qx < xdim) && (qy >= 0) && (qy < ydim))
/* 186:    */                   {
/* 187:212 */                     int[] fq = this.input.getVXYByte(qx, qy);
/* 188:    */                     
/* 189:214 */                     rlval[0] = Math.abs(fDatumP[0] - fq[0]);
/* 190:215 */                     rlval[1] = Math.abs(fDatumP[1] - fq[1]);
/* 191:216 */                     rlval[2] = Math.abs(fDatumP[2] - fq[2]);
/* 192:    */                     
/* 193:218 */                     int lblq = lbl.getXYInt(qx, qy);
/* 194:220 */                     if ((lblq > 0) && (lblq != lblval) && (rlcrt >= rlval[0]) && (rlcrt >= rlval[1]) && (rlcrt >= rlval[2]))
/* 195:    */                     {
/* 196:221 */                       rlcrt = maxDim(rlval) - 1;
/* 197:223 */                       if (rcrt > rlcrt)
/* 198:    */                       {
/* 199:224 */                         for (Point pp : stack) {
/* 200:225 */                           rl.setXYByte(pp.x, pp.y, 255);
/* 201:    */                         }
/* 202:227 */                         stack.clear();
/* 203:    */                         
/* 204:229 */                         Collection<ArrayList<Point>> pointLists = pq.values();
/* 205:    */                         Iterator localIterator6;
/* 206:231 */                         for (Iterator localIterator5 = pointLists.iterator(); localIterator5.hasNext(); localIterator6.hasNext())
/* 207:    */                         {
/* 208:231 */                           Object pointList = (ArrayList)localIterator5.next();
/* 209:    */                           
/* 210:233 */                           localIterator6 = ((ArrayList)pointList).iterator(); continue;Point p = (Point)localIterator6.next();
/* 211:234 */                           rl.setXYByte(p.x, p.y, 255);
/* 212:    */                         }
/* 213:237 */                         pq.clear();
/* 214:238 */                         break;
/* 215:    */                       }
/* 216:    */                     }
/* 217:    */                     else
/* 218:    */                     {
/* 219:243 */                       int rlq = rl.getXYByte(qx, qy);
/* 220:245 */                       if (((rlval[0] <= rlcrt) || (rlval[1] <= rlcrt) || (rlval[2] <= rlcrt)) && ((rlval[0] < rlq) || (rlval[1] < rlq) || (rlval[2] < rlq))) {
/* 221:248 */                         if ((rlval[0] < rlq) && (rlval[1] < rlq) && (rlval[2] < rlq))
/* 222:    */                         {
/* 223:249 */                           rl.setXYByte(qx, qy, maxDim(rlval));
/* 224:251 */                           if (pq.containsKey(Integer.valueOf(maxDim(rlval))))
/* 225:    */                           {
/* 226:252 */                             ((ArrayList)pq.get(Integer.valueOf(maxDim(rlval)))).add(new Point(qx, qy));
/* 227:    */                           }
/* 228:    */                           else
/* 229:    */                           {
/* 230:254 */                             Object tmp2 = new ArrayList();
/* 231:255 */                             ((ArrayList)tmp2).add(new Point(qx, qy));
/* 232:256 */                             pq.put(Integer.valueOf(maxDim(rlval)), tmp2);
/* 233:    */                           }
/* 234:    */                         }
/* 235:    */                       }
/* 236:    */                     }
/* 237:    */                   }
/* 238:    */                 }
/* 239:    */               }
/* 240:    */             }
/* 241:    */           }
/* 242:262 */           while (!stack.isEmpty())
/* 243:    */           {
/* 244:263 */             Point sPoint = (Point)stack.pop();
/* 245:264 */             lbl.setXYInt(sPoint.x, sPoint.y, lblval);
/* 246:    */           }
/* 247:267 */           lblval++;
/* 248:    */         }
/* 249:    */       }
/* 250:    */     }
/* 251:272 */     System.out.println("Marginal Soille : Number of labels " + (lblval - 1));
/* 252:    */     
/* 253:    */ 
/* 254:    */ 
/* 255:    */ 
/* 256:    */ 
/* 257:    */ 
/* 258:    */ 
/* 259:    */ 
/* 260:    */ 
/* 261:282 */     this.output = lbl;
/* 262:    */   }
/* 263:    */   
/* 264:    */   private int maxDim(int[] d)
/* 265:    */   {
/* 266:291 */     int max = 0;
/* 267:293 */     for (int i = 0; i < d.length; i++) {
/* 268:294 */       if (d[i] > max) {
/* 269:295 */         max = d[i];
/* 270:    */       }
/* 271:    */     }
/* 272:298 */     return max;
/* 273:    */   }
/* 274:    */   
/* 275:    */   private int minDim(int[] d)
/* 276:    */   {
/* 277:307 */     int min = 2147483647;
/* 278:309 */     for (int i = 0; i < d.length; i++) {
/* 279:310 */       if (d[i] < min) {
/* 280:311 */         min = d[i];
/* 281:    */       }
/* 282:    */     }
/* 283:314 */     return min;
/* 284:    */   }
/* 285:    */   
/* 286:    */   public static Image invoke(Image img, int alpha, int omega)
/* 287:    */   {
/* 288:    */     try
/* 289:    */     {
/* 290:319 */       return (Image)new ColorQFZSoille().preprocess(new Object[] { img, Integer.valueOf(alpha), Integer.valueOf(omega) });
/* 291:    */     }
/* 292:    */     catch (GlobalException e)
/* 293:    */     {
/* 294:321 */       e.printStackTrace();
/* 295:    */     }
/* 296:322 */     return null;
/* 297:    */   }
/* 298:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.color.ColorQFZSoille
 * JD-Core Version:    0.7.0.1
 */