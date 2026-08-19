/*   1:    */ package vpt.algorithms.io;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileInputStream;
/*   6:    */ import java.io.FileNotFoundException;
/*   7:    */ import java.io.IOException;
/*   8:    */ import java.io.InputStream;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.nio.ByteBuffer;
/*  11:    */ import vpt.Algorithm;
/*  12:    */ import vpt.DoubleImage;
/*  13:    */ import vpt.GlobalException;
/*  14:    */ import vpt.Image;
/*  15:    */ 
/*  16:    */ public class LoadFLO
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19: 27 */   public Image output = null;
/*  20: 28 */   public String path = null;
/*  21: 30 */   private boolean debug = false;
/*  22:    */   
/*  23:    */   public LoadFLO()
/*  24:    */   {
/*  25: 33 */     this.inputFields = "path";
/*  26: 34 */     this.outputFields = "output";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void execute()
/*  30:    */     throws GlobalException
/*  31:    */   {
/*  32: 39 */     byte[] data = read(this.path);
/*  33: 42 */     if (bytes2Float(data, 0) != 202021.25D) {
/*  34: 42 */       throw new GlobalException("Sanity check failed");
/*  35:    */     }
/*  36: 45 */     int width = (int)bytes2Integer(data, 4);
/*  37: 46 */     if (this.debug) {
/*  38: 46 */       System.err.println(width);
/*  39:    */     }
/*  40: 49 */     int height = (int)bytes2Integer(data, 8);
/*  41: 50 */     if (this.debug) {
/*  42: 50 */       System.err.println(height);
/*  43:    */     }
/*  44: 52 */     if ((width == 0) || (height == 0)) {
/*  45: 52 */       throw new GlobalException("Invalid image size");
/*  46:    */     }
/*  47: 53 */     this.output = new DoubleImage(width, height, 1);
/*  48: 54 */     this.output.fill(0.0D);
/*  49:    */     
/*  50:    */ 
/*  51: 57 */     int x = 0;
/*  52: 58 */     int y = 0;
/*  53: 61 */     for (int i = 12; i < data.length; i += 8)
/*  54:    */     {
/*  55: 64 */       float u = bytes2Float(data, i);
/*  56:    */       
/*  57:    */ 
/*  58: 67 */       float v = bytes2Float(data, i + 4);
/*  59: 69 */       if (this.debug) {
/*  60: 69 */         System.err.println(u + " " + v + " " + (Math.atan2(v, u) + 3.141592653589793D));
/*  61:    */       }
/*  62: 71 */       this.output.setXYDouble(x, y, (Math.atan2(v, u) + 3.141592653589793D) / 6.283185307179586D);
/*  63:    */       
/*  64: 73 */       x = (x + 1) % width;
/*  65: 74 */       if (x % width == 0) {
/*  66: 74 */         y++;
/*  67:    */       }
/*  68:    */     }
/*  69:    */   }
/*  70:    */   
/*  71:    */   private float bytes2Float(byte[] data, int index)
/*  72:    */   {
/*  73: 80 */     byte[] datum = new byte[4];
/*  74: 81 */     ByteBuffer bbDatum = ByteBuffer.allocate(4);
/*  75: 83 */     for (int i = index; i < index + 4; i++) {
/*  76: 84 */       datum[(3 - (i - index))] = data[i];
/*  77:    */     }
/*  78: 87 */     bbDatum.put(datum);
/*  79: 88 */     return bbDatum.getFloat(0);
/*  80:    */   }
/*  81:    */   
/*  82:    */   private float bytes2Integer(byte[] data, int index)
/*  83:    */   {
/*  84: 93 */     byte[] datum = new byte[4];
/*  85: 94 */     ByteBuffer bbDatum = ByteBuffer.allocate(4);
/*  86: 96 */     for (int i = index; i < index + 4; i++) {
/*  87: 97 */       datum[(3 - (i - index))] = data[i];
/*  88:    */     }
/*  89:100 */     bbDatum.put(datum);
/*  90:101 */     return bbDatum.getInt(0);
/*  91:    */   }
/*  92:    */   
/*  93:    */   private byte[] read(String path)
/*  94:    */   {
/*  95:105 */     if (this.debug) {
/*  96:105 */       System.err.println("Reading file...");
/*  97:    */     }
/*  98:106 */     File file = new File(path);
/*  99:108 */     if (this.debug) {
/* 100:108 */       System.err.println("File size " + file.length());
/* 101:    */     }
/* 102:109 */     byte[] result = new byte[(int)file.length()];
/* 103:    */     try
/* 104:    */     {
/* 105:112 */       InputStream input = null;
/* 106:    */       try
/* 107:    */       {
/* 108:114 */         int totalBytesRead = 0;
/* 109:115 */         input = new BufferedInputStream(new FileInputStream(file));
/* 110:116 */         while (totalBytesRead < result.length)
/* 111:    */         {
/* 112:117 */           int bytesRemaining = result.length - totalBytesRead;
/* 113:    */           
/* 114:    */ 
/* 115:120 */           int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
/* 116:121 */           if (bytesRead > 0) {
/* 117:122 */             totalBytesRead += bytesRead;
/* 118:    */           }
/* 119:    */         }
/* 120:126 */         if (this.debug) {
/* 121:126 */           System.err.println("Bytes read " + totalBytesRead);
/* 122:    */         }
/* 123:    */       }
/* 124:    */       finally
/* 125:    */       {
/* 126:129 */         if (this.debug) {
/* 127:129 */           System.err.println("Closing");
/* 128:    */         }
/* 129:130 */         input.close();
/* 130:    */       }
/* 131:    */     }
/* 132:    */     catch (FileNotFoundException ex)
/* 133:    */     {
/* 134:134 */       System.err.println("File not found");
/* 135:    */     }
/* 136:    */     catch (IOException ex)
/* 137:    */     {
/* 138:137 */       ex.printStackTrace();
/* 139:    */     }
/* 140:139 */     return result;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public static Image invoke(String path)
/* 144:    */   {
/* 145:    */     try
/* 146:    */     {
/* 147:153 */       return (Image)new LoadFLO().preprocess(new Object[] { path });
/* 148:    */     }
/* 149:    */     catch (GlobalException e)
/* 150:    */     {
/* 151:156 */       e.printStackTrace();
/* 152:    */     }
/* 153:157 */     return null;
/* 154:    */   }
/* 155:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.io.LoadFLO
 * JD-Core Version:    0.7.0.1
 */