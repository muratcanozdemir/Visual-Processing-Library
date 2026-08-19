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
/*  13:    */ public class GrayFZ
/*  14:    */   extends Algorithm
/*  15:    */ {
/*  16:    */   public Image input;
/*  17:    */   public IntegerImage output;
/*  18: 27 */   private ArrayList<Point> list2 = new ArrayList();
/*  19: 28 */   private Stack<Point> s = new Stack();
/*  20:    */   private int xdim;
/*  21:    */   private int ydim;
/*  22:    */   BooleanImage temp;
/*  23: 40 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  24: 41 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  25:    */   
/*  26:    */   public GrayFZ()
/*  27:    */   {
/*  28: 44 */     this.inputFields = "input";
/*  29: 45 */     this.outputFields = "output";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void execute()
/*  33:    */     throws GlobalException
/*  34:    */   {
/*  35: 50 */     this.xdim = this.input.getXDim();
/*  36: 51 */     this.ydim = this.input.getYDim();
/*  37: 53 */     if (this.input.getCDim() != 1) {
/*  38: 53 */       throw new GlobalException("This implementation is only for grayscale images.");
/*  39:    */     }
/*  40: 55 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  41: 56 */     this.output.fill(0);
/*  42:    */     
/*  43: 58 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  44: 59 */     this.temp.fill(false);
/*  45:    */     
/*  46: 61 */     int label = 1;
/*  47: 63 */     for (int x = 0; x < this.xdim; x++)
/*  48:    */     {
/*  49: 66 */       System.out.println("Sutun " + x);
/*  50: 68 */       for (int y = 0; y < this.ydim; y++) {
/*  51: 71 */         if (this.output.getXYInt(x, y) <= 0)
/*  52:    */         {
/*  53: 73 */           Point t = new Point(x, y);
/*  54:    */           
/*  55: 75 */           createFZ(t, label++);
/*  56:    */         }
/*  57:    */       }
/*  58:    */     }
/*  59: 79 */     System.out.println("Total number of quasi flat zones: " + (label - 1));
/*  60:    */   }
/*  61:    */   
/*  62:    */   private void createFZ(Point r, int label)
/*  63:    */   {
/*  64: 90 */     this.s.clear();
/*  65: 91 */     this.s.add(r);
/*  66: 92 */     this.temp.setXYBoolean(r.x, r.y, true);
/*  67: 93 */     this.list2.add(r);
/*  68:    */     int x;
/*  69:    */     int i;
/*  70: 95 */     for (; !this.s.isEmpty(); i < this.N.length)
/*  71:    */     {
/*  72: 97 */       Point tmp = (Point)this.s.pop();
/*  73:    */       
/*  74: 99 */       x = tmp.x;
/*  75:100 */       int y = tmp.y;
/*  76:    */       
/*  77:102 */       int p = this.input.getXYByte(x, y);
/*  78:103 */       this.output.setXYInt(x, y, label);
/*  79:    */       
/*  80:105 */       i = 0; continue;
/*  81:106 */       int _x = x + this.N[i].x;
/*  82:107 */       int _y = y + this.N[i].y;
/*  83:109 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/*  84:111 */         if (!this.temp.getXYBoolean(_x, _y))
/*  85:    */         {
/*  86:113 */           int q = this.input.getXYByte(_x, _y);
/*  87:115 */           if (q == p)
/*  88:    */           {
/*  89:117 */             Point t = new Point(_x, _y);
/*  90:118 */             this.s.add(t);
/*  91:    */             
/*  92:120 */             this.temp.setXYBoolean(t.x, t.y, true);
/*  93:121 */             this.list2.add(t);
/*  94:    */           }
/*  95:    */         }
/*  96:    */       }
/*  97:105 */       i++;
/*  98:    */     }
/*  99:126 */     for (Point t : this.list2) {
/* 100:127 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 101:    */     }
/* 102:128 */     this.list2.clear();
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static Image invoke(Image img)
/* 106:    */   {
/* 107:    */     try
/* 108:    */     {
/* 109:133 */       return (IntegerImage)new GrayFZ().preprocess(new Object[] { img });
/* 110:    */     }
/* 111:    */     catch (GlobalException e)
/* 112:    */     {
/* 113:135 */       e.printStackTrace();
/* 114:    */     }
/* 115:136 */     return null;
/* 116:    */   }
/* 117:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.gray.GrayFZ
 * JD-Core Version:    0.7.0.1
 */