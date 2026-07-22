/*   1:    */ package vpt.algorithms.segmentation;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.Vector;
/*   6:    */ import vpt.Algorithm;
/*   7:    */ import vpt.GlobalException;
/*   8:    */ import vpt.Image;
/*   9:    */ import vpt.IntegerImage;
/*  10:    */ 
/*  11:    */ public class Watershed
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14:    */   public Image input;
/*  15:    */   public Image output;
/*  16:    */   private int xDim;
/*  17:    */   private int yDim;
/*  18:    */   private int cDim;
/*  19:    */   public static final int WSHED = 0;
/*  20:    */   private static final int INIT = -3;
/*  21:    */   private static final int MASK = -2;
/*  22:    */   private static final int INQUEUE = -1;
/*  23:    */   
/*  24:    */   public Watershed()
/*  25:    */   {
/*  26: 35 */     this.inputFields = "input";
/*  27: 36 */     this.outputFields = "output";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void execute()
/*  31:    */     throws GlobalException
/*  32:    */   {
/*  33: 41 */     this.xDim = this.input.getXDim();
/*  34: 42 */     this.yDim = this.input.getYDim();
/*  35: 43 */     this.cDim = this.input.getCDim();
/*  36:    */     
/*  37:    */ 
/*  38: 46 */     this.output = new IntegerImage(this.xDim, this.yDim, this.cDim);
/*  39: 47 */     this.output.fill(-3);
/*  40: 49 */     for (int c = 0; c < this.cDim; c++)
/*  41:    */     {
/*  42: 51 */       boolean flag = false;
/*  43:    */       
/*  44: 53 */       Fifo fifo = new Fifo();
/*  45:    */       
/*  46:    */ 
/*  47: 56 */       int currentLabel = 0;
/*  48:    */       
/*  49:    */ 
/*  50:    */ 
/*  51: 60 */       Vector[] distro = calculateInputPixelDistro(c);
/*  52: 62 */       for (int i = 0; i < 256; i++)
/*  53:    */       {
/*  54: 65 */         int size = distro[i].size();
/*  55: 67 */         for (int j = 0; j < size; j++)
/*  56:    */         {
/*  57: 68 */           Point p = (Point)distro[i].elementAt(j);
/*  58:    */           
/*  59: 70 */           int x = p.x;
/*  60: 71 */           int y = p.y;
/*  61:    */           
/*  62: 73 */           this.output.setXYCInt(x, y, c, -2);
/*  63: 75 */           if (areThereLabelledNeighbours(x, y, c))
/*  64:    */           {
/*  65: 76 */             this.output.setXYCInt(x, y, c, -1);
/*  66: 77 */             fifo.add(p);
/*  67:    */           }
/*  68:    */         }
/*  69:    */         int y;
/*  70:    */         int j;
/*  71: 81 */         for (; !fifo.isEmpty(); j <= y + 1)
/*  72:    */         {
/*  73: 82 */           Point p = fifo.retrieve();
/*  74: 83 */           int x = p.x;
/*  75: 84 */           y = p.y;
/*  76:    */           
/*  77:    */ 
/*  78: 87 */           j = y - 1; continue;
/*  79: 88 */           for (int k = x - 1; k <= x + 1; k++) {
/*  80: 90 */             if ((k >= 0) && (k < this.xDim) && (j >= 0) && (j < this.yDim)) {
/*  81: 93 */               if (((j != y) || (k != x)) && (this.output.getXYCInt(k, j, c) > 0))
/*  82:    */               {
/*  83: 94 */                 if ((this.output.getXYCInt(x, y, c) == -1) || ((this.output.getXYCInt(x, y, c) == 0) && (flag)))
/*  84:    */                 {
/*  85: 95 */                   this.output.setXYCInt(x, y, c, this.output.getXYCInt(k, j, c));
/*  86:    */                 }
/*  87: 97 */                 else if ((this.output.getXYCInt(x, y, c) > 0) && 
/*  88: 98 */                   (this.output.getXYCInt(x, y, c) != this.output.getXYCInt(k, j, c)))
/*  89:    */                 {
/*  90: 99 */                   this.output.setXYCInt(x, y, c, 0);
/*  91:100 */                   flag = false;
/*  92:    */                 }
/*  93:    */               }
/*  94:102 */               else if ((this.output.getXYCInt(k, j, c) == 0) && (this.output.getXYCInt(x, y, c) == -1))
/*  95:    */               {
/*  96:103 */                 this.output.setXYCInt(x, y, c, 0);
/*  97:104 */                 flag = true;
/*  98:    */               }
/*  99:105 */               else if (this.output.getXYCInt(k, j, c) == -2)
/* 100:    */               {
/* 101:106 */                 this.output.setXYCInt(k, j, c, -1);
/* 102:107 */                 fifo.add(new Point(k, j));
/* 103:    */               }
/* 104:    */             }
/* 105:    */           }
/* 106: 87 */           j++;
/* 107:    */         }
/* 108:115 */         size = distro[i].size();
/* 109:117 */         for (int j = 0; j < size; j++)
/* 110:    */         {
/* 111:118 */           Point p = (Point)distro[i].elementAt(j);
/* 112:    */           
/* 113:120 */           int x = p.x;
/* 114:121 */           int y = p.y;
/* 115:123 */           if (this.output.getXYCInt(x, y, c) == -2)
/* 116:    */           {
/* 117:124 */             currentLabel++;
/* 118:125 */             fifo.add(p);
/* 119:126 */             this.output.setXYCInt(x, y, c, currentLabel);
/* 120:    */             int l;
/* 121:128 */             for (; !fifo.isEmpty(); l <= y + 1)
/* 122:    */             {
/* 123:129 */               p = fifo.retrieve();
/* 124:130 */               x = p.x;
/* 125:131 */               y = p.y;
/* 126:    */               
/* 127:    */ 
/* 128:134 */               l = y - 1; continue;
/* 129:135 */               for (int k = x - 1; k <= x + 1; k++) {
/* 130:136 */                 if ((k >= 0) && (k < this.xDim) && (l >= 0) && (l < this.yDim) && 
/* 131:137 */                   ((k != x) || (l != y)) && (this.output.getXYCInt(k, l, c) == -2))
/* 132:    */                 {
/* 133:138 */                   fifo.add(new Point(k, l));
/* 134:139 */                   this.output.setXYCInt(k, l, c, currentLabel);
/* 135:    */                 }
/* 136:    */               }
/* 137:134 */               l++;
/* 138:    */             }
/* 139:    */           }
/* 140:    */         }
/* 141:    */       }
/* 142:147 */       System.err.println("Number of labels:" + --currentLabel);
/* 143:    */     }
/* 144:    */   }
/* 145:    */   
/* 146:    */   private Vector<Point>[] calculateInputPixelDistro(int c)
/* 147:    */   {
/* 148:152 */     Vector[] distro = new Vector[256];
/* 149:154 */     for (int i = 0; i < 256; i++) {
/* 150:155 */       distro[i] = new Vector();
/* 151:    */     }
/* 152:157 */     for (int x = 0; x < this.xDim; x++) {
/* 153:158 */       for (int y = 0; y < this.yDim; y++) {
/* 154:159 */         distro[this.input.getXYCByte(x, y, c)].add(new Point(x, y));
/* 155:    */       }
/* 156:    */     }
/* 157:162 */     return distro;
/* 158:    */   }
/* 159:    */   
/* 160:    */   private boolean areThereLabelledNeighbours(int x, int y, int c)
/* 161:    */   {
/* 162:167 */     for (int j = y - 1; j <= y + 1; j++) {
/* 163:168 */       for (int i = x - 1; i <= x + 1; i++) {
/* 164:169 */         if ((i >= 0) && (i < this.xDim) && (j >= 0) && (j < this.yDim)) {
/* 165:171 */           if (((i != x) || (j != y)) && (this.output.getXYCInt(i, j, c) >= 0)) {
/* 166:172 */             return true;
/* 167:    */           }
/* 168:    */         }
/* 169:    */       }
/* 170:    */     }
/* 171:176 */     return false;
/* 172:    */   }
/* 173:    */   
/* 174:    */   private class Fifo
/* 175:    */   {
/* 176:    */     private Vector<Point> v;
/* 177:    */     
/* 178:    */     Fifo()
/* 179:    */     {
/* 180:183 */       this.v = new Vector();
/* 181:    */     }
/* 182:    */     
/* 183:    */     void add(Point p)
/* 184:    */     {
/* 185:187 */       this.v.add(p);
/* 186:    */     }
/* 187:    */     
/* 188:    */     Point retrieve()
/* 189:    */     {
/* 190:191 */       Point p = (Point)this.v.firstElement();
/* 191:192 */       this.v.remove(0);
/* 192:    */       
/* 193:194 */       return p;
/* 194:    */     }
/* 195:    */     
/* 196:    */     boolean isEmpty()
/* 197:    */     {
/* 198:198 */       return this.v.size() == 0;
/* 199:    */     }
/* 200:    */   }
/* 201:    */   
/* 202:    */   public static Image invoke(Image input)
/* 203:    */   {
/* 204:    */     try
/* 205:    */     {
/* 206:204 */       return (Image)new Watershed().preprocess(new Object[] { input });
/* 207:    */     }
/* 208:    */     catch (GlobalException e)
/* 209:    */     {
/* 210:206 */       e.printStackTrace();
/* 211:    */     }
/* 212:207 */     return null;
/* 213:    */   }
/* 214:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.Watershed
 * JD-Core Version:    0.7.0.1
 */