/*   1:    */ package vpt.algorithms.shape;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.BooleanImage;
/*   7:    */ import vpt.GlobalException;
/*   8:    */ import vpt.Image;
/*   9:    */ import vpt.algorithms.mm.binary.BInternGradient;
/*  10:    */ import vpt.algorithms.mm.binary.BOpening;
/*  11:    */ import vpt.algorithms.segmentation.Threshold;
/*  12:    */ import vpt.util.se.FlatSE;
/*  13:    */ 
/*  14:    */ public class DominantPointsDetection
/*  15:    */   extends Algorithm
/*  16:    */ {
/*  17: 23 */   public Point[] output = null;
/*  18: 24 */   public Image input = null;
/*  19: 25 */   public Double epsilon = null;
/*  20:    */   
/*  21:    */   public DominantPointsDetection()
/*  22:    */   {
/*  23: 28 */     this.inputFields = "input, epsilon";
/*  24: 29 */     this.outputFields = "output";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void execute()
/*  28:    */     throws GlobalException
/*  29:    */   {
/*  30: 33 */     if (this.input.getCDim() != 1) {
/*  31: 33 */       throw new GlobalException("Sorry, only mono-channel images for now...");
/*  32:    */     }
/*  33: 36 */     Image mask = Threshold.invoke(this.input, 0.00392156862745098D);
/*  34:    */     
/*  35:    */ 
/*  36: 39 */     mask = BOpening.invoke(mask, FlatSE.square(3));
/*  37:    */     
/*  38:    */ 
/*  39: 42 */     int xdim = mask.getXDim();
/*  40: 43 */     int ydim = mask.getYDim();
/*  41: 44 */     Image temp = new BooleanImage(xdim + 2, ydim + 2, 1);
/*  42: 46 */     for (int x = 0; x < xdim; x++) {
/*  43: 47 */       for (int y = 0; y < ydim; y++) {
/*  44: 48 */         if (mask.getXYBoolean(x, y)) {
/*  45: 49 */           temp.setXYBoolean(x + 1, y + 1, true);
/*  46:    */         }
/*  47:    */       }
/*  48:    */     }
/*  49: 53 */     mask = temp;
/*  50:    */     
/*  51:    */ 
/*  52: 56 */     Image outerBorders = BInternGradient.invoke(mask, FlatSE.square(3));
/*  53:    */     
/*  54:    */ 
/*  55: 59 */     ArrayList<Point> points = new ArrayList();
/*  56:    */     
/*  57: 61 */     Point p = null;
/*  58:    */     
/*  59: 63 */     xdim = outerBorders.getXDim();
/*  60: 64 */     ydim = outerBorders.getYDim();
/*  61: 67 */     for (int x = 0; x < xdim; x++)
/*  62:    */     {
/*  63: 68 */       for (int y = 0; y < ydim; y++) {
/*  64: 69 */         if (outerBorders.getXYBoolean(x, y))
/*  65:    */         {
/*  66: 70 */           p = new Point(x, y);
/*  67: 71 */           break;
/*  68:    */         }
/*  69:    */       }
/*  70: 74 */       if (p != null) {
/*  71:    */         break;
/*  72:    */       }
/*  73:    */     }
/*  74: 77 */     Image flag = outerBorders.newInstance(false);
/*  75: 78 */     flag.fill(false);
/*  76:    */     for (;;)
/*  77:    */     {
/*  78: 81 */       points.add(new Point(p.x, p.y));
/*  79: 82 */       flag.setXYBoolean(p.x, p.y, true);
/*  80: 85 */       if ((p.x + 1 < xdim) && (outerBorders.getXYBoolean(p.x + 1, p.y)) && (!flag.getXYBoolean(p.x + 1, p.y)))
/*  81:    */       {
/*  82: 86 */         p.x += 1;
/*  83:    */       }
/*  84: 87 */       else if ((p.y + 1 < ydim) && (outerBorders.getXYBoolean(p.x, p.y + 1)) && (!flag.getXYBoolean(p.x, p.y + 1)))
/*  85:    */       {
/*  86: 88 */         p.y += 1;
/*  87:    */       }
/*  88: 89 */       else if ((p.x - 1 >= 0) && (outerBorders.getXYBoolean(p.x - 1, p.y)) && (!flag.getXYBoolean(p.x - 1, p.y)))
/*  89:    */       {
/*  90: 90 */         p.x -= 1;
/*  91:    */       }
/*  92:    */       else
/*  93:    */       {
/*  94: 91 */         if ((p.y - 1 < 0) || (!outerBorders.getXYBoolean(p.x, p.y - 1)) || (flag.getXYBoolean(p.x, p.y - 1))) {
/*  95:    */           break;
/*  96:    */         }
/*  97: 92 */         p.y -= 1;
/*  98:    */       }
/*  99:    */     }
/* 100: 97 */     points = properRDP(points, this.epsilon.doubleValue());
/* 101:    */     
/* 102:    */ 
/* 103:100 */     this.output = new Point[points.size()];
/* 104:101 */     for (int i = 0; i < this.output.length; i++)
/* 105:    */     {
/* 106:102 */       Point q = (Point)points.get(i);
/* 107:103 */       this.output[i] = new Point(q.x - 1, q.y - 1);
/* 108:    */     }
/* 109:    */   }
/* 110:    */   
/* 111:    */   private ArrayList<Point> properRDP(ArrayList<Point> points, double epsilon)
/* 112:    */   {
/* 113:108 */     Point firstPoint = (Point)points.get(0);
/* 114:109 */     Point lastPoint = (Point)points.get(points.size() - 1);
/* 115:110 */     ArrayList<Point> result = null;
/* 116:112 */     if (points.size() < 3) {
/* 117:113 */       return points;
/* 118:    */     }
/* 119:115 */     int index = -1;
/* 120:116 */     double dist = 0.0D;
/* 121:118 */     for (int i = 1; i < points.size() - 1; i++)
/* 122:    */     {
/* 123:119 */       double cDist = findPerpendicularDistance((Point)points.get(i), firstPoint, lastPoint);
/* 124:120 */       if (cDist > dist)
/* 125:    */       {
/* 126:121 */         dist = cDist;
/* 127:122 */         index = i;
/* 128:    */       }
/* 129:    */     }
/* 130:126 */     if (dist >= epsilon)
/* 131:    */     {
/* 132:127 */       ArrayList<Point> r1 = properRDP(getSubList(points, 0, index), epsilon);
/* 133:128 */       ArrayList<Point> r2 = properRDP(getSubList(points, index, points.size() - 1), epsilon);
/* 134:    */       
/* 135:    */ 
/* 136:131 */       result = r1;
/* 137:132 */       join(result, r2);
/* 138:    */     }
/* 139:    */     else
/* 140:    */     {
/* 141:134 */       result = new ArrayList();
/* 142:135 */       result.add(firstPoint);
/* 143:136 */       result.add(lastPoint);
/* 144:    */     }
/* 145:139 */     return result;
/* 146:    */   }
/* 147:    */   
/* 148:    */   private void join(ArrayList<Point> list1, ArrayList<Point> list2)
/* 149:    */   {
/* 150:144 */     for (Point p : list2)
/* 151:    */     {
/* 152:145 */       boolean exists = false;
/* 153:147 */       for (Point q : list1) {
/* 154:148 */         if ((q.x == p.x) && (q.y == p.y))
/* 155:    */         {
/* 156:149 */           exists = true;
/* 157:150 */           break;
/* 158:    */         }
/* 159:    */       }
/* 160:153 */       if (!exists) {
/* 161:153 */         list1.add(p);
/* 162:    */       }
/* 163:    */     }
/* 164:    */   }
/* 165:    */   
/* 166:    */   private ArrayList<Point> getSubList(ArrayList<Point> list, int beg, int end)
/* 167:    */   {
/* 168:159 */     ArrayList<Point> result = new ArrayList();
/* 169:161 */     for (int i = beg; i <= end; i++) {
/* 170:162 */       result.add((Point)list.get(i));
/* 171:    */     }
/* 172:164 */     return result;
/* 173:    */   }
/* 174:    */   
/* 175:    */   private double findPerpendicularDistance(Point p, Point p1, Point p2)
/* 176:    */   {
/* 177:170 */     double area = Math.abs(0.5D * (p1.x * p2.y + p2.x * p.y + p.x * p1.y - p2.x * p1.y - p.x * p2.y - p1.x * p.y));
/* 178:171 */     double bottom = Math.sqrt(Math.pow(p1.x - p2.x, 2.0D) + Math.pow(p1.y - p2.y, 2.0D));
/* 179:172 */     double height = area / bottom * 2.0D;
/* 180:    */     
/* 181:174 */     return height;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public static Point[] invoke(Image img, Double epsilon)
/* 185:    */   {
/* 186:    */     try
/* 187:    */     {
/* 188:180 */       return (Point[])new DominantPointsDetection().preprocess(new Object[] { img, epsilon });
/* 189:    */     }
/* 190:    */     catch (GlobalException e)
/* 191:    */     {
/* 192:182 */       e.printStackTrace();
/* 193:    */     }
/* 194:183 */     return null;
/* 195:    */   }
/* 196:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.DominantPointsDetection
 * JD-Core Version:    0.7.0.1
 */