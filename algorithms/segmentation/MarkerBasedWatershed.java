/*   1:    */ package vpt.algorithms.segmentation;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.LinkedList;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.BooleanImage;
/*   7:    */ import vpt.GlobalException;
/*   8:    */ import vpt.Image;
/*   9:    */ import vpt.IntegerImage;
/*  10:    */ 
/*  11:    */ public class MarkerBasedWatershed
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14:    */   public Image inputImage;
/*  15:    */   public Image outputImage;
/*  16:    */   private Image mask;
/*  17: 30 */   private boolean connexity4 = false;
/*  18: 32 */   private final int IGNORE = -1;
/*  19: 33 */   private final int NULL = 0;
/*  20: 34 */   private final int GRAY_LEVELS = 256;
/*  21:    */   
/*  22:    */   public MarkerBasedWatershed()
/*  23:    */   {
/*  24: 37 */     this.inputFields = "inputImage";
/*  25: 38 */     this.outputFields = "outputImage";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void execute()
/*  29:    */     throws GlobalException
/*  30:    */   {
/*  31: 42 */     this.mask = new BooleanImage(this.inputImage, false);
/*  32: 43 */     this.mask.fill(1);
/*  33:    */     
/*  34: 45 */     int cdim = this.inputImage.getCDim();
/*  35: 46 */     int xdim = this.inputImage.getXDim();
/*  36: 47 */     int ydim = this.inputImage.getYDim();
/*  37:    */     
/*  38: 49 */     IntegerImage input = new IntegerImage(xdim, ydim, 1);
/*  39: 50 */     IntegerImage output = new IntegerImage(xdim, ydim, 1);
/*  40:    */     
/*  41: 52 */     this.outputImage = new IntegerImage(xdim, ydim, cdim);
/*  42: 53 */     for (int c = 0; c < cdim; c++)
/*  43:    */     {
/*  44: 55 */       for (int x = 0; x < xdim; x++) {
/*  45: 56 */         for (int y = 0; y < ydim; y++) {
/*  46: 57 */           input.setXYCInt(x, y, 0, this.inputImage.getXYCByte(x, y, c));
/*  47:    */         }
/*  48:    */       }
/*  49: 59 */       HierarchicalQueue queue = new HierarchicalQueue(256);
/*  50: 60 */       int currentLabel = 1;
/*  51:    */       
/*  52: 62 */       output.fill(0);
/*  53: 66 */       for (int x = 0; x < xdim; x++) {
/*  54: 67 */         for (int y = 0; y < ydim; y++)
/*  55:    */         {
/*  56: 68 */           int p = input.getXYInt(x, y);
/*  57: 69 */           int v = output.getXYInt(x, y);
/*  58: 71 */           if (!this.mask.getXYBoolean(x, y))
/*  59:    */           {
/*  60: 72 */             output.setXYInt(x, y, -1);
/*  61:    */           }
/*  62: 74 */           else if ((p == 0) && (v == 0))
/*  63:    */           {
/*  64: 75 */             marker(input, output, x, y, queue, currentLabel);
/*  65: 76 */             currentLabel++;
/*  66:    */           }
/*  67:    */         }
/*  68:    */       }
/*  69:    */       Point[] neighbours;
/*  70:    */       int i;
/*  71: 83 */       for (; !queue.isEmpty(); i < neighbours.length)
/*  72:    */       {
/*  73: 84 */         Point p = queue.get();
/*  74: 85 */         int label = output.getXYInt(p.x, p.y);
/*  75:    */         
/*  76:    */ 
/*  77: 88 */         neighbours = getNonLabelledNeighbours(output, p.x, p.y);
/*  78:    */         
/*  79: 90 */         i = 0; continue;
/*  80:    */         
/*  81: 92 */         output.setXYInt(neighbours[i].x, neighbours[i].y, label);
/*  82:    */         
/*  83:    */ 
/*  84: 95 */         int val = input.getXYInt(neighbours[i].x, neighbours[i].y);
/*  85:    */         
/*  86:    */ 
/*  87: 98 */         queue.add(neighbours[i], val);i++;
/*  88:    */       }
/*  89:103 */       for (int _x = 0; _x < xdim; _x++) {
/*  90:104 */         for (int _y = 0; _y < ydim; _y++) {
/*  91:105 */           this.outputImage.setXYCInt(_x, _y, c, output.getXYCInt(_x, _y, 0));
/*  92:    */         }
/*  93:    */       }
/*  94:    */     }
/*  95:    */   }
/*  96:    */   
/*  97:    */   private void marker(IntegerImage input, IntegerImage output, int x, int y, HierarchicalQueue queue, int label)
/*  98:    */   {
/*  99:112 */     LinkedList<Point> fifo = new LinkedList();
/* 100:    */     
/* 101:114 */     fifo.add(new Point(x, y));
/* 102:    */     Point p;
/* 103:    */     int j;
/* 104:116 */     for (; fifo.size() > 0; j <= p.y + 1)
/* 105:    */     {
/* 106:117 */       p = (Point)fifo.removeFirst();
/* 107:    */       
/* 108:119 */       queue.add(p, 0);
/* 109:120 */       output.setXYInt(p.x, p.y, label);
/* 110:    */       
/* 111:122 */       j = p.y - 1; continue;
/* 112:123 */       for (int k = p.x - 1; k <= p.x + 1; k++) {
/* 113:124 */         if ((k >= 0) && (k < input.getXDim()) && (j >= 0) && (j < input.getYDim())) {
/* 114:127 */           if ((!this.connexity4) || (j == 0) || (k == 0)) {
/* 115:130 */             if (this.mask.getXYBoolean(k, j)) {
/* 116:133 */               if (((k != p.x) || (j != p.y)) && (input.getXYInt(k, j) == 0) && (output.getXYInt(k, j) == 0))
/* 117:    */               {
/* 118:134 */                 int size = fifo.size();
/* 119:135 */                 boolean b = false;
/* 120:137 */                 for (int i = 0; i < size; i++)
/* 121:    */                 {
/* 122:138 */                   Point v = (Point)fifo.get(i);
/* 123:139 */                   if ((v.x == k) && (v.y == j)) {
/* 124:140 */                     b = true;
/* 125:    */                   }
/* 126:    */                 }
/* 127:143 */                 if (!b) {
/* 128:144 */                   fifo.add(new Point(k, j));
/* 129:    */                 }
/* 130:    */               }
/* 131:    */             }
/* 132:    */           }
/* 133:    */         }
/* 134:    */       }
/* 135:122 */       j++;
/* 136:    */     }
/* 137:    */   }
/* 138:    */   
/* 139:    */   private Point[] getNonLabelledNeighbours(IntegerImage output, int x, int y)
/* 140:    */   {
/* 141:152 */     Point[] neighbours = new Point[8];
/* 142:    */     
/* 143:154 */     int cnt = 0;
/* 144:156 */     for (int j = y - 1; j <= y + 1; j++) {
/* 145:157 */       for (int i = x - 1; i <= x + 1; i++) {
/* 146:158 */         if ((i >= 0) && (i < output.getXDim()) && (j >= 0) && (j < output.getYDim())) {
/* 147:161 */           if ((!this.connexity4) || (i == 0) || (j == 0)) {
/* 148:164 */             if (this.mask.getXYBoolean(i, j))
/* 149:    */             {
/* 150:167 */               int z = output.getXYInt(i, j);
/* 151:169 */               if (((i != x) || (j != y)) && (z == 0)) {
/* 152:170 */                 neighbours[(cnt++)] = new Point(i, j);
/* 153:    */               }
/* 154:    */             }
/* 155:    */           }
/* 156:    */         }
/* 157:    */       }
/* 158:    */     }
/* 159:175 */     if (cnt < 8)
/* 160:    */     {
/* 161:176 */       Point[] tmp = new Point[cnt];
/* 162:178 */       for (int i = 0; i < cnt; i++) {
/* 163:179 */         tmp[i] = neighbours[i];
/* 164:    */       }
/* 165:181 */       neighbours = tmp;
/* 166:    */     }
/* 167:184 */     return neighbours;
/* 168:    */   }
/* 169:    */   
/* 170:    */   class HierarchicalQueue
/* 171:    */   {
/* 172:    */     private LinkedList<Point>[] queue;
/* 173:    */     private int current;
/* 174:    */     
/* 175:    */     HierarchicalQueue(int size)
/* 176:    */     {
/* 177:193 */       this.queue = new LinkedList[size];
/* 178:195 */       for (int i = 0; i < size; i++) {
/* 179:196 */         this.queue[i] = new LinkedList();
/* 180:    */       }
/* 181:198 */       this.current = 0;
/* 182:    */     }
/* 183:    */     
/* 184:    */     void add(Point p, int val)
/* 185:    */     {
/* 186:202 */       if (val >= this.current) {
/* 187:203 */         this.queue[val].add(p);
/* 188:    */       } else {
/* 189:205 */         this.queue[this.current].add(p);
/* 190:    */       }
/* 191:    */     }
/* 192:    */     
/* 193:    */     Point get()
/* 194:    */     {
/* 195:209 */       if (this.queue[this.current].size() >= 2) {
/* 196:210 */         return (Point)this.queue[this.current].removeFirst();
/* 197:    */       }
/* 198:212 */       if (this.queue[this.current].size() == 1)
/* 199:    */       {
/* 200:213 */         Point p = (Point)this.queue[this.current].removeFirst();
/* 201:215 */         while ((this.current < 255) && (this.queue[this.current].size() == 0)) {
/* 202:216 */           this.current += 1;
/* 203:    */         }
/* 204:218 */         return p;
/* 205:    */       }
/* 206:221 */       return null;
/* 207:    */     }
/* 208:    */     
/* 209:    */     boolean isEmpty()
/* 210:    */     {
/* 211:225 */       int sum = 0;
/* 212:227 */       for (int i = this.current; i < this.queue.length; i++) {
/* 213:228 */         sum += this.queue[i].size();
/* 214:    */       }
/* 215:230 */       return sum == 0;
/* 216:    */     }
/* 217:    */   }
/* 218:    */   
/* 219:    */   public static Image invoke(Image input)
/* 220:    */   {
/* 221:    */     try
/* 222:    */     {
/* 223:236 */       return (Image)new MarkerBasedWatershed().preprocess(new Object[] { input });
/* 224:    */     }
/* 225:    */     catch (GlobalException e)
/* 226:    */     {
/* 227:238 */       e.printStackTrace();
/* 228:    */     }
/* 229:239 */     return null;
/* 230:    */   }
/* 231:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.MarkerBasedWatershed
 * JD-Core Version:    0.7.0.1
 */