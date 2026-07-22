/*   1:    */ package vpt.algorithms.segmentation;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.Stack;
/*   8:    */ import vpt.Algorithm;
/*   9:    */ import vpt.BooleanImage;
/*  10:    */ import vpt.GlobalException;
/*  11:    */ import vpt.Image;
/*  12:    */ import vpt.IntegerImage;
/*  13:    */ 
/*  14:    */ public class MP
/*  15:    */   extends Algorithm
/*  16:    */ {
/*  17:    */   public Image map;
/*  18:    */   public Image reference;
/*  19:    */   public Double mp;
/*  20:    */   
/*  21:    */   public MP()
/*  22:    */   {
/*  23: 34 */     this.inputFields = "map,reference";
/*  24: 35 */     this.outputFields = "mp";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void execute()
/*  28:    */     throws GlobalException
/*  29:    */   {
/*  30: 40 */     int xdim = this.map.getXDim();
/*  31: 41 */     int ydim = this.map.getYDim();
/*  32:    */     
/*  33:    */ 
/*  34: 44 */     IntegerImage regionLabels = new IntegerImage(xdim, ydim, 1);
/*  35: 45 */     regionLabels.fill(-1);
/*  36:    */     
/*  37:    */ 
/*  38: 48 */     int numRegions = numberOfFZ(this.reference);
/*  39: 49 */     int[] votes = new int[numRegions];
/*  40:    */     
/*  41: 51 */     ArrayList<Point> list = new ArrayList();
/*  42: 52 */     ArrayList<Point> list2 = new ArrayList();
/*  43: 53 */     Stack<Point> s = new Stack();
/*  44:    */     
/*  45: 55 */     BooleanImage temp = new BooleanImage(xdim, ydim, 1);
/*  46: 56 */     temp.fill(false);
/*  47:    */     
/*  48:    */ 
/*  49: 59 */     Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  50: 60 */       new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  51: 62 */     for (int y = 0; y < ydim; y++) {
/*  52: 63 */       for (int x = 0; x < xdim; x++)
/*  53:    */       {
/*  54: 65 */         int p = this.map.getXYInt(x, y);
/*  55: 68 */         if (regionLabels.getXYInt(x, y) <= -1)
/*  56:    */         {
/*  57: 71 */           Arrays.fill(votes, 0);
/*  58:    */           
/*  59:    */ 
/*  60:    */ 
/*  61: 75 */           Point r = new Point(x, y);
/*  62:    */           
/*  63: 77 */           s.clear();
/*  64: 78 */           s.add(r);
/*  65: 79 */           temp.setXYBoolean(r.x, r.y, true);
/*  66: 80 */           list.add(r);
/*  67: 82 */           for (; !s.isEmpty(); i < N.length)
/*  68:    */           {
/*  69: 84 */             Point tmp = (Point)s.pop();
/*  70:    */             
/*  71: 86 */             _p = this.reference.getXYInt(tmp.x, tmp.y);
/*  72: 87 */             votes[_p] += 1;
/*  73: 88 */             list2.add(tmp);
/*  74:    */             
/*  75: 90 */             i = 0; continue;
/*  76: 91 */             int _x = tmp.x + N[i].x;
/*  77: 92 */             int _y = tmp.y + N[i].y;
/*  78: 94 */             if ((_x >= 0) && (_x < xdim) && (_y >= 0) && (_y < ydim)) {
/*  79: 96 */               if (!temp.getXYBoolean(_x, _y))
/*  80:    */               {
/*  81: 98 */                 int q = this.map.getXYInt(_x, _y);
/*  82:100 */                 if (q == p)
/*  83:    */                 {
/*  84:102 */                   Point t = new Point(_x, _y);
/*  85:103 */                   s.add(t);
/*  86:    */                   
/*  87:105 */                   temp.setXYBoolean(t.x, t.y, true);
/*  88:106 */                   list.add(t);
/*  89:    */                 }
/*  90:    */               }
/*  91:    */             }
/*  92: 90 */             i++;
/*  93:    */           }
/*  94:    */           Point t;
/*  95:111 */           for (int _p = list.iterator(); _p.hasNext(); temp.setXYBoolean(t.x, t.y, false)) {
/*  96:111 */             t = (Point)_p.next();
/*  97:    */           }
/*  98:112 */           list.clear();
/*  99:    */           
/* 100:    */ 
/* 101:    */ 
/* 102:116 */           int region = 0;
/* 103:117 */           for (int i = 1; i < votes.length; i++) {
/* 104:118 */             if (votes[i] > votes[region]) {
/* 105:119 */               region = i;
/* 106:    */             }
/* 107:    */           }
/* 108:    */           Point t;
/* 109:124 */           for (int i = list2.iterator(); i.hasNext(); regionLabels.setXYInt(t.x, t.y, region)) {
/* 110:124 */             t = (Point)i.next();
/* 111:    */           }
/* 112:125 */           list2.clear();
/* 113:    */         }
/* 114:    */       }
/* 115:    */     }
/* 116:132 */     int count = 0;
/* 117:134 */     for (int y = 0; y < ydim; y++) {
/* 118:135 */       for (int x = 0; x < xdim; x++)
/* 119:    */       {
/* 120:136 */         int p = regionLabels.getXYInt(x, y);
/* 121:137 */         int q = this.reference.getXYInt(x, y);
/* 122:139 */         if (p == q) {
/* 123:139 */           count++;
/* 124:    */         }
/* 125:    */       }
/* 126:    */     }
/* 127:143 */     this.mp = Double.valueOf(count / (xdim * ydim));
/* 128:    */   }
/* 129:    */   
/* 130:    */   private int numberOfFZ(Image img)
/* 131:    */   {
/* 132:147 */     int xdim = img.getXDim();
/* 133:148 */     int ydim = img.getYDim();
/* 134:    */     
/* 135:150 */     Image temp2 = new IntegerImage(xdim, ydim, 1);
/* 136:151 */     temp2.fill(0);
/* 137:    */     
/* 138:153 */     BooleanImage temp = new BooleanImage(xdim, ydim, 1);
/* 139:154 */     temp.fill(false);
/* 140:    */     
/* 141:156 */     int label = 1;
/* 142:158 */     for (int x = 0; x < xdim; x++) {
/* 143:160 */       for (int y = 0; y < ydim; y++) {
/* 144:162 */         if (temp2.getXYInt(x, y) <= 0)
/* 145:    */         {
/* 146:164 */           Point t = new Point(x, y);
/* 147:    */           
/* 148:166 */           createFZ(img, t, label++, temp, temp2, xdim, ydim);
/* 149:    */         }
/* 150:    */       }
/* 151:    */     }
/* 152:170 */     return label - 1;
/* 153:    */   }
/* 154:    */   
/* 155:    */   private void createFZ(Image input, Point r, int label, BooleanImage temp, Image temp2, int xdim, int ydim)
/* 156:    */   {
/* 157:175 */     ArrayList<Point> list2 = new ArrayList();
/* 158:176 */     Stack<Point> s = new Stack();
/* 159:    */     
/* 160:    */ 
/* 161:179 */     Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/* 162:180 */       new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/* 163:    */     
/* 164:182 */     s.clear();
/* 165:183 */     s.add(r);
/* 166:184 */     temp.setXYBoolean(r.x, r.y, true);
/* 167:185 */     list2.add(r);
/* 168:    */     int x;
/* 169:    */     int i;
/* 170:187 */     for (; !s.isEmpty(); i < N.length)
/* 171:    */     {
/* 172:189 */       Point tmp = (Point)s.pop();
/* 173:    */       
/* 174:191 */       x = tmp.x;
/* 175:192 */       int y = tmp.y;
/* 176:    */       
/* 177:194 */       int p = input.getXYInt(x, y);
/* 178:195 */       temp2.setXYInt(x, y, label);
/* 179:    */       
/* 180:197 */       i = 0; continue;
/* 181:198 */       int _x = x + N[i].x;
/* 182:199 */       int _y = y + N[i].y;
/* 183:201 */       if ((_x >= 0) && (_x < xdim) && (_y >= 0) && (_y < ydim)) {
/* 184:203 */         if (!temp.getXYBoolean(_x, _y))
/* 185:    */         {
/* 186:205 */           int q = input.getXYInt(_x, _y);
/* 187:207 */           if (q == p)
/* 188:    */           {
/* 189:209 */             Point t = new Point(_x, _y);
/* 190:210 */             s.add(t);
/* 191:    */             
/* 192:212 */             temp.setXYBoolean(t.x, t.y, true);
/* 193:213 */             list2.add(t);
/* 194:    */           }
/* 195:    */         }
/* 196:    */       }
/* 197:197 */       i++;
/* 198:    */     }
/* 199:218 */     for (Point t : list2) {
/* 200:219 */       temp.setXYBoolean(t.x, t.y, false);
/* 201:    */     }
/* 202:220 */     list2.clear();
/* 203:    */   }
/* 204:    */   
/* 205:    */   public static Double invoke(Image map, Image reference)
/* 206:    */   {
/* 207:    */     try
/* 208:    */     {
/* 209:225 */       return (Double)new MP().preprocess(new Object[] { map, reference });
/* 210:    */     }
/* 211:    */     catch (GlobalException e)
/* 212:    */     {
/* 213:227 */       e.printStackTrace();
/* 214:    */     }
/* 215:228 */     return null;
/* 216:    */   }
/* 217:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.MP
 * JD-Core Version:    0.7.0.1
 */