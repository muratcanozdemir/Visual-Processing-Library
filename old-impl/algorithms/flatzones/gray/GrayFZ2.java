/*   1:    */ package vpt.algorithms.flatzones.gray;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Stack;
/*   7:    */ import vpt.Algorithm;
/*   8:    */ import vpt.BooleanImage;
/*   9:    */ import vpt.GlobalException;
/*  10:    */ import vpt.Image;
/*  11:    */ import vpt.IntegerImage;
/*  12:    */ 
/*  13:    */ public class GrayFZ2
/*  14:    */   extends Algorithm
/*  15:    */ {
/*  16:    */   public Image input;
/*  17:    */   public ArrayList<ArrayList<Point>> output;
/*  18:    */   private Image tempOutput;
/*  19: 29 */   private Stack<Point> s = new Stack();
/*  20:    */   private int xdim;
/*  21:    */   private int ydim;
/*  22:    */   BooleanImage temp;
/*  23: 41 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  24: 42 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  25:    */   
/*  26:    */   public GrayFZ2()
/*  27:    */   {
/*  28: 45 */     this.inputFields = "input";
/*  29: 46 */     this.outputFields = "output";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void execute()
/*  33:    */     throws GlobalException
/*  34:    */   {
/*  35: 51 */     this.xdim = this.input.getXDim();
/*  36: 52 */     this.ydim = this.input.getYDim();
/*  37: 54 */     if (this.input.getCDim() != 1) {
/*  38: 54 */       throw new GlobalException("This implementation is only for grayscale images.");
/*  39:    */     }
/*  40: 56 */     this.output = new ArrayList();
/*  41:    */     
/*  42: 58 */     this.tempOutput = new IntegerImage(this.xdim, this.ydim, 1);
/*  43: 59 */     this.tempOutput.fill(0);
/*  44:    */     
/*  45: 61 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  46: 62 */     this.temp.fill(false);
/*  47:    */     
/*  48: 64 */     int label = 1;
/*  49: 66 */     for (int x = 0; x < this.xdim; x++)
/*  50:    */     {
/*  51: 69 */       System.out.println("Sutun " + x);
/*  52: 71 */       for (int y = 0; y < this.ydim; y++) {
/*  53: 74 */         if (this.tempOutput.getXYInt(x, y) <= 0)
/*  54:    */         {
/*  55: 76 */           Point t = new Point(x, y);
/*  56:    */           
/*  57: 78 */           createFZ(t, label++);
/*  58:    */         }
/*  59:    */       }
/*  60:    */     }
/*  61: 82 */     System.out.println("Total number of quasi flat zones: " + (label - 1));
/*  62:    */   }
/*  63:    */   
/*  64:    */   private void createFZ(Point r, int label)
/*  65:    */   {
/*  66: 93 */     this.s.clear();
/*  67: 94 */     this.s.add(r);
/*  68: 95 */     this.temp.setXYBoolean(r.x, r.y, true);
/*  69: 96 */     ArrayList<Point> list2 = new ArrayList();
/*  70: 97 */     list2.add(r);
/*  71:    */     int x;
/*  72:    */     int i;
/*  73: 99 */     for (; !this.s.isEmpty(); i < this.N.length)
/*  74:    */     {
/*  75:101 */       Point tmp = (Point)this.s.pop();
/*  76:    */       
/*  77:103 */       x = tmp.x;
/*  78:104 */       int y = tmp.y;
/*  79:    */       
/*  80:106 */       int p = this.input.getXYByte(x, y);
/*  81:107 */       this.tempOutput.setXYInt(x, y, label);
/*  82:    */       
/*  83:109 */       i = 0; continue;
/*  84:110 */       int _x = x + this.N[i].x;
/*  85:111 */       int _y = y + this.N[i].y;
/*  86:113 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/*  87:115 */         if (!this.temp.getXYBoolean(_x, _y))
/*  88:    */         {
/*  89:117 */           int q = this.input.getXYByte(_x, _y);
/*  90:119 */           if (q == p)
/*  91:    */           {
/*  92:121 */             Point t = new Point(_x, _y);
/*  93:122 */             this.s.add(t);
/*  94:    */             
/*  95:124 */             this.temp.setXYBoolean(t.x, t.y, true);
/*  96:125 */             list2.add(t);
/*  97:    */           }
/*  98:    */         }
/*  99:    */       }
/* 100:109 */       i++;
/* 101:    */     }
/* 102:130 */     this.output.add(list2);
/* 103:132 */     for (Point t : list2) {
/* 104:133 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public static ArrayList<ArrayList<Point>> invoke(Image img)
/* 109:    */   {
/* 110:    */     try
/* 111:    */     {
/* 112:138 */       return (ArrayList)new GrayFZ2().preprocess(new Object[] { img });
/* 113:    */     }
/* 114:    */     catch (GlobalException e)
/* 115:    */     {
/* 116:140 */       e.printStackTrace();
/* 117:    */     }
/* 118:141 */     return null;
/* 119:    */   }
/* 120:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.gray.GrayFZ2
 * JD-Core Version:    0.7.0.1
 */