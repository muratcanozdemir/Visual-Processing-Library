/*   1:    */ package vpt.algorithms.flatzones.gray;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.Stack;
/*   8:    */ import java.util.TreeMap;
/*   9:    */ import vpt.Algorithm;
/*  10:    */ import vpt.ByteImage;
/*  11:    */ import vpt.GlobalException;
/*  12:    */ import vpt.Image;
/*  13:    */ import vpt.IntegerImage;
/*  14:    */ 
/*  15:    */ public class GrayQFZSoille
/*  16:    */   extends Algorithm
/*  17:    */ {
/*  18:    */   public Image input;
/*  19:    */   public IntegerImage output;
/*  20:    */   public Integer alpha;
/*  21:    */   public Integer omega;
/*  22: 30 */   public Point[] neighbors4 = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1) };
/*  23: 31 */   public Point[] neighbors8 = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  24: 32 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  25:    */   
/*  26:    */   public GrayQFZSoille()
/*  27:    */   {
/*  28: 35 */     this.inputFields = "input,alpha,omega";
/*  29: 36 */     this.outputFields = "output";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void execute()
/*  33:    */     throws GlobalException
/*  34:    */   {
/*  35: 41 */     int xdim = this.input.getXDim();
/*  36: 42 */     int ydim = this.input.getYDim();
/*  37:    */     
/*  38:    */ 
/*  39: 45 */     Point[] N = this.neighbors8;
/*  40: 47 */     if (this.input.getCDim() != 1) {
/*  41: 47 */       throw new GlobalException("This implementation is only for grayscale images.");
/*  42:    */     }
/*  43: 48 */     if ((this.alpha.intValue() < 0) || (this.alpha.intValue() > 254) || (this.omega.intValue() < 0) || (this.omega.intValue() > 254)) {
/*  44: 48 */       throw new GlobalException("Arguments out of range.");
/*  45:    */     }
/*  46: 51 */     IntegerImage lbl = new IntegerImage(this.input, false);
/*  47: 52 */     lbl.fill(0);
/*  48:    */     
/*  49:    */ 
/*  50: 55 */     ByteImage rl = new ByteImage(this.input, false);
/*  51: 56 */     rl.fill(255);
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56:    */ 
/*  57: 62 */     Stack<Point> stack = new Stack();
/*  58:    */     
/*  59:    */ 
/*  60:    */ 
/*  61: 66 */     TreeMap<Integer, ArrayList<Point>> pq = new TreeMap();
/*  62:    */     
/*  63: 68 */     int lblval = 1;
/*  64: 70 */     for (int x = 0; x < xdim; x++) {
/*  65: 71 */       for (int y = 0; y < ydim; y++) {
/*  66: 72 */         if (lbl.getXYInt(x, y) == 0)
/*  67:    */         {
/*  68: 73 */           lbl.setXYInt(x, y, lblval);
/*  69: 74 */           int fp = this.input.getXYByte(x, y);
/*  70: 75 */           int maxcc = fp;
/*  71: 76 */           int mincc = maxcc;
/*  72: 77 */           int rlcrt = this.alpha.intValue();
/*  73: 79 */           for (int i = 0; i < N.length; i++)
/*  74:    */           {
/*  75: 81 */             int qx = x + N[i].x;
/*  76: 82 */             int qy = y + N[i].y;
/*  77: 84 */             if ((qx >= 0) && (qx < xdim) && (qy >= 0) && (qy < ydim))
/*  78:    */             {
/*  79: 86 */               int rlval = Math.abs(fp - this.input.getXYByte(qx, qy));
/*  80: 88 */               if (lbl.getXYInt(qx, qy) > 0)
/*  81:    */               {
/*  82: 89 */                 if (rlcrt >= rlval) {
/*  83: 90 */                   rlcrt = rlval - 1;
/*  84:    */                 }
/*  85:    */               }
/*  86: 95 */               else if (rlval <= rlcrt)
/*  87:    */               {
/*  88: 96 */                 rl.setXYByte(qx, qy, rlval);
/*  89: 98 */                 if (pq.containsKey(Integer.valueOf(rlval)))
/*  90:    */                 {
/*  91: 99 */                   ((ArrayList)pq.get(Integer.valueOf(rlval))).add(new Point(qx, qy));
/*  92:    */                 }
/*  93:    */                 else
/*  94:    */                 {
/*  95:101 */                   ArrayList<Point> tmp = new ArrayList();
/*  96:102 */                   tmp.add(new Point(qx, qy));
/*  97:103 */                   pq.put(Integer.valueOf(rlval), tmp);
/*  98:    */                 }
/*  99:    */               }
/* 100:    */             }
/* 101:    */           }
/* 102:    */           int rcrt;
/* 103:    */           int rcrt;
/* 104:108 */           if (!pq.isEmpty()) {
/* 105:108 */             rcrt = ((Integer)pq.firstKey()).intValue();
/* 106:    */           } else {
/* 107:109 */             rcrt = 0;
/* 108:    */           }
/* 109:111 */           while (!pq.isEmpty())
/* 110:    */           {
/* 111:112 */             int datumPrio = ((Integer)pq.firstKey()).intValue();
/* 112:113 */             ArrayList<Point> tmp = (ArrayList)pq.get(Integer.valueOf(datumPrio));
/* 113:114 */             Point datumP = (Point)tmp.remove(0);
/* 114:116 */             if (tmp.isEmpty()) {
/* 115:116 */               pq.pollFirstEntry();
/* 116:    */             }
/* 117:118 */             if (lbl.getXYInt(datumP.x, datumP.y) <= 0) {
/* 118:120 */               if (datumPrio > rcrt)
/* 119:    */               {
/* 120:121 */                 while (!stack.isEmpty())
/* 121:    */                 {
/* 122:122 */                   Point sPoint = (Point)stack.pop();
/* 123:123 */                   lbl.setXYInt(sPoint.x, sPoint.y, lblval);
/* 124:    */                 }
/* 125:126 */                 rcrt = datumPrio;
/* 126:128 */                 if (lbl.getXYInt(datumP.x, datumP.y) > 0) {}
/* 127:    */               }
/* 128:    */               else
/* 129:    */               {
/* 130:131 */                 stack.add(datumP);
/* 131:    */                 
/* 132:133 */                 int fDatumP = this.input.getXYByte(datumP.x, datumP.y);
/* 133:135 */                 if (fDatumP < mincc) {
/* 134:135 */                   mincc = fDatumP;
/* 135:    */                 }
/* 136:136 */                 if (fDatumP > maxcc) {
/* 137:136 */                   maxcc = fDatumP;
/* 138:    */                 }
/* 139:138 */                 if ((this.omega.intValue() < maxcc - mincc) || (rcrt > rlcrt))
/* 140:    */                 {
/* 141:140 */                   for (Point pp : stack) {
/* 142:141 */                     rl.setXYByte(pp.x, pp.y, 255);
/* 143:    */                   }
/* 144:143 */                   stack.clear();
/* 145:    */                   
/* 146:145 */                   Collection<ArrayList<Point>> pointLists = pq.values();
/* 147:    */                   Iterator localIterator3;
/* 148:147 */                   for (Iterator localIterator2 = pointLists.iterator(); localIterator2.hasNext(); localIterator3.hasNext())
/* 149:    */                   {
/* 150:147 */                     Object pointList = (ArrayList)localIterator2.next();
/* 151:    */                     
/* 152:149 */                     localIterator3 = ((ArrayList)pointList).iterator(); continue;Point p = (Point)localIterator3.next();
/* 153:150 */                     rl.setXYByte(p.x, p.y, 255);
/* 154:    */                   }
/* 155:153 */                   pq.clear();
/* 156:154 */                   break;
/* 157:    */                 }
/* 158:157 */                 for (int i = 0; i < N.length; i++)
/* 159:    */                 {
/* 160:159 */                   int qx = datumP.x + N[i].x;
/* 161:160 */                   int qy = datumP.y + N[i].y;
/* 162:162 */                   if ((qx >= 0) && (qx < xdim) && (qy >= 0) && (qy < ydim))
/* 163:    */                   {
/* 164:164 */                     int rlval = Math.abs(fDatumP - this.input.getXYByte(qx, qy));
/* 165:    */                     
/* 166:166 */                     int lblq = lbl.getXYInt(qx, qy);
/* 167:168 */                     if ((lblq > 0) && (lblq != lblval) && (rlcrt >= rlval))
/* 168:    */                     {
/* 169:169 */                       rlcrt = rlval - 1;
/* 170:171 */                       if (rcrt > rlcrt)
/* 171:    */                       {
/* 172:172 */                         for (Point pp : stack) {
/* 173:173 */                           rl.setXYByte(pp.x, pp.y, 255);
/* 174:    */                         }
/* 175:175 */                         stack.clear();
/* 176:    */                         
/* 177:177 */                         Object pointLists = pq.values();
/* 178:    */                         Iterator localIterator6;
/* 179:179 */                         for (Iterator localIterator5 = ((Collection)pointLists).iterator(); localIterator5.hasNext(); localIterator6.hasNext())
/* 180:    */                         {
/* 181:179 */                           Object pointList = (ArrayList)localIterator5.next();
/* 182:    */                           
/* 183:181 */                           localIterator6 = ((ArrayList)pointList).iterator(); continue;Point p = (Point)localIterator6.next();
/* 184:182 */                           rl.setXYByte(p.x, p.y, 255);
/* 185:    */                         }
/* 186:185 */                         pq.clear();
/* 187:186 */                         break;
/* 188:    */                       }
/* 189:    */                     }
/* 190:191 */                     else if ((rlval <= rlcrt) && (rlval < rl.getXYByte(qx, qy)) && 
/* 191:192 */                       (rlval < rl.getXYByte(qx, qy)))
/* 192:    */                     {
/* 193:193 */                       rl.setXYByte(qx, qy, rlval);
/* 194:195 */                       if (pq.containsKey(Integer.valueOf(rlval)))
/* 195:    */                       {
/* 196:196 */                         ((ArrayList)pq.get(Integer.valueOf(rlval))).add(new Point(qx, qy));
/* 197:    */                       }
/* 198:    */                       else
/* 199:    */                       {
/* 200:198 */                         Object tmp2 = new ArrayList();
/* 201:199 */                         ((ArrayList)tmp2).add(new Point(qx, qy));
/* 202:200 */                         pq.put(Integer.valueOf(rlval), tmp2);
/* 203:    */                       }
/* 204:    */                     }
/* 205:    */                   }
/* 206:    */                 }
/* 207:    */               }
/* 208:    */             }
/* 209:    */           }
/* 210:206 */           while (!stack.isEmpty())
/* 211:    */           {
/* 212:207 */             Point sPoint = (Point)stack.pop();
/* 213:208 */             lbl.setXYInt(sPoint.x, sPoint.y, lblval);
/* 214:    */           }
/* 215:211 */           lblval++;
/* 216:    */         }
/* 217:    */       }
/* 218:    */     }
/* 219:219 */     this.output = lbl;
/* 220:    */   }
/* 221:    */   
/* 222:    */   public static Image invoke(Image img, int alpha, int omega)
/* 223:    */   {
/* 224:    */     try
/* 225:    */     {
/* 226:225 */       return (IntegerImage)new GrayQFZSoille().preprocess(new Object[] { img, Integer.valueOf(alpha), Integer.valueOf(omega) });
/* 227:    */     }
/* 228:    */     catch (GlobalException e)
/* 229:    */     {
/* 230:227 */       e.printStackTrace();
/* 231:    */     }
/* 232:228 */     return null;
/* 233:    */   }
/* 234:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.gray.GrayQFZSoille
 * JD-Core Version:    0.7.0.1
 */