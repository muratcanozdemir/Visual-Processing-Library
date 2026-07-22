/*   1:    */ package vpt.algorithms.mm.gray.adaptive;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Stack;
/*   6:    */ import vpt.Algorithm;
/*   7:    */ import vpt.ByteImage;
/*   8:    */ import vpt.GlobalException;
/*   9:    */ import vpt.Image;
/*  10:    */ import vpt.algorithms.display.Display2D;
/*  11:    */ import vpt.algorithms.io.Load;
/*  12:    */ import vpt.util.Tools;
/*  13:    */ import vpt.util.se.FlatSE;
/*  14:    */ 
/*  15:    */ public class ComputeSEs
/*  16:    */   extends Algorithm
/*  17:    */ {
/*  18: 24 */   public FlatSE[][] output = null;
/*  19: 25 */   public Image input = null;
/*  20: 26 */   public Integer size = null;
/*  21: 27 */   private Stack<Point> s = new Stack();
/*  22: 28 */   private ArrayList<Point> list = new ArrayList();
/*  23: 30 */   private Image tmpImg = null;
/*  24:    */   private int xdim;
/*  25:    */   private int ydim;
/*  26:    */   private int cdim;
/*  27:    */   
/*  28:    */   public ComputeSEs()
/*  29:    */   {
/*  30: 35 */     this.inputFields = "input,size";
/*  31: 36 */     this.outputFields = "output";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void execute()
/*  35:    */     throws GlobalException
/*  36:    */   {
/*  37: 41 */     this.xdim = this.input.getXDim();
/*  38: 42 */     this.ydim = this.input.getYDim();
/*  39: 43 */     this.cdim = this.input.getCDim();
/*  40:    */     
/*  41: 45 */     this.tmpImg = new ByteImage(this.input, false);
/*  42: 46 */     this.tmpImg.fill(0);
/*  43: 48 */     if (this.cdim != 1) {
/*  44: 48 */       throw new GlobalException("Cannot handle non grayscale images yet...");
/*  45:    */     }
/*  46: 50 */     this.output = new FlatSE[this.xdim][this.ydim];
/*  47: 53 */     for (int y = 0; y < this.ydim; y++) {
/*  48: 54 */       for (int x = 0; x < this.xdim; x++) {
/*  49: 57 */         this.output[x][y] = computeAmoeba(new Point(x, y));
/*  50:    */       }
/*  51:    */     }
/*  52:    */   }
/*  53:    */   
/*  54:    */   private FlatSE computeAmoeba(Point r)
/*  55:    */   {
/*  56: 67 */     FlatSE adaptedSE = FlatSE.square(this.size.intValue() * 2 + 1);
/*  57: 68 */     adaptedSE.fill(false);
/*  58:    */     
/*  59:    */ 
/*  60: 71 */     this.s.clear();
/*  61:    */     
/*  62:    */ 
/*  63: 74 */     this.s.add(r);
/*  64:    */     int x;
/*  65:    */     int i;
/*  66: 76 */     for (; !this.s.isEmpty(); i < Tools.N.length)
/*  67:    */     {
/*  68: 78 */       Point tmp = (Point)this.s.pop();
/*  69:    */       
/*  70: 80 */       x = tmp.x;
/*  71: 81 */       int y = tmp.y;
/*  72:    */       
/*  73:    */ 
/*  74: 84 */       int x_p = this.size.intValue() - (x - r.x);
/*  75: 85 */       int y_p = this.size.intValue() - (y - r.y);
/*  76:    */       
/*  77:    */ 
/*  78: 88 */       adaptedSE.setXYBoolean(x_p, y_p, true);
/*  79: 91 */       if (this.tmpImg.getXYByte(x, y) == 0)
/*  80:    */       {
/*  81: 92 */         this.tmpImg.setXYByte(x, y, 1);
/*  82: 93 */         this.list.add(tmp);
/*  83:    */       }
/*  84: 97 */       int p = this.input.getXYByte(x, y);
/*  85:    */       
/*  86:    */ 
/*  87:100 */       i = 0; continue;
/*  88:101 */       int _x = x + Tools.N[i].x;
/*  89:102 */       int _y = y + Tools.N[i].y;
/*  90:105 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/*  91:    */       {
/*  92:107 */         int q = this.input.getXYByte(_x, _y);
/*  93:    */         
/*  94:109 */         int L = (int)(this.tmpImg.getXYByte(x, y) + 1 + 0.25D * Math.abs(p - q));
/*  95:113 */         if ((this.tmpImg.getXYByte(_x, _y) <= 0) || (this.tmpImg.getXYByte(_x, _y) > L)) {
/*  96:122 */           if (L <= this.size.intValue() / 2)
/*  97:    */           {
/*  98:126 */             this.tmpImg.setXYByte(_x, _y, L);
/*  99:127 */             Point t = new Point(_x, _y);
/* 100:128 */             this.s.add(t);
/* 101:129 */             this.list.add(t);
/* 102:    */           }
/* 103:    */         }
/* 104:    */       }
/* 105:100 */       i++;
/* 106:    */     }
/* 107:135 */     for (Point t : this.list) {
/* 108:136 */       this.tmpImg.setXYByte(t.x, t.y, 0);
/* 109:    */     }
/* 110:137 */     this.list.clear();
/* 111:    */     
/* 112:139 */     return adaptedSE;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static void main(String[] args)
/* 116:    */   {
/* 117:143 */     Image img = Load.invoke("samples/radial_gradient.png");
/* 118:    */     
/* 119:    */ 
/* 120:146 */     FlatSE[][] ses = invoke(img, Integer.valueOf(23));
/* 121:    */     
/* 122:148 */     Point[] coords = ses[15][15].getCoords();
/* 123:150 */     for (Point p : coords)
/* 124:    */     {
/* 125:151 */       int _x = 15 - p.x;
/* 126:152 */       int _y = 15 - p.y;
/* 127:    */       
/* 128:154 */       img.setXYByte(_x, _y, 255);
/* 129:    */     }
/* 130:157 */     coords = ses[45][45].getCoords();
/* 131:159 */     for (Point p : coords)
/* 132:    */     {
/* 133:160 */       int _x = 45 - p.x;
/* 134:161 */       int _y = 45 - p.y;
/* 135:    */       
/* 136:163 */       img.setXYByte(_x, _y, 255);
/* 137:    */     }
/* 138:166 */     coords = ses[95][40].getCoords();
/* 139:168 */     for (Point p : coords)
/* 140:    */     {
/* 141:169 */       int _x = 95 - p.x;
/* 142:170 */       int _y = 40 - p.y;
/* 143:    */       
/* 144:172 */       img.setXYByte(_x, _y, 255);
/* 145:    */     }
/* 146:175 */     coords = ses[60][100].getCoords();
/* 147:177 */     for (Point p : coords)
/* 148:    */     {
/* 149:178 */       int _x = 60 - p.x;
/* 150:179 */       int _y = 100 - p.y;
/* 151:    */       
/* 152:181 */       img.setXYByte(_x, _y, 255);
/* 153:    */     }
/* 154:184 */     Display2D.invoke(img);
/* 155:    */   }
/* 156:    */   
/* 157:    */   public static FlatSE[][] invoke(Image image, Integer size)
/* 158:    */   {
/* 159:    */     try
/* 160:    */     {
/* 161:190 */       return (FlatSE[][])new ComputeSEs().preprocess(new Object[] { image, size });
/* 162:    */     }
/* 163:    */     catch (GlobalException e)
/* 164:    */     {
/* 165:192 */       e.printStackTrace();
/* 166:    */     }
/* 167:193 */     return null;
/* 168:    */   }
/* 169:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.adaptive.ComputeSEs
 * JD-Core Version:    0.7.0.1
 */