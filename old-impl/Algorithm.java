/*   1:    */ package vpt;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Field;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ 
/*   6:    */ public abstract class Algorithm
/*   7:    */ {
/*   8:    */   protected String inputFields;
/*   9:    */   protected String outputFields;
/*  10:    */   private ArrayList<Object> output;
/*  11:    */   
/*  12:    */   private void setInputFields(Object... params)
/*  13:    */     throws GlobalException
/*  14:    */   {
/*  15: 24 */     String[] inputFieldNames = getFields(this.inputFields);
/*  16: 27 */     if (inputFieldNames.length != params.length) {
/*  17: 28 */       throw new GlobalException("Requires " + inputFieldNames.length + " parameters!");
/*  18:    */     }
/*  19: 32 */     Field field = null;
/*  20: 34 */     for (int i = 0; i < params.length; i++)
/*  21:    */     {
/*  22:    */       try
/*  23:    */       {
/*  24: 37 */         field = getClass().getDeclaredField(inputFieldNames[i]);
/*  25:    */       }
/*  26:    */       catch (SecurityException e)
/*  27:    */       {
/*  28: 38 */         e.printStackTrace();
/*  29:    */       }
/*  30:    */       catch (NoSuchFieldException e)
/*  31:    */       {
/*  32: 39 */         e.printStackTrace();
/*  33:    */       }
/*  34:    */       try
/*  35:    */       {
/*  36: 43 */         field.set(this, params[i]);
/*  37:    */       }
/*  38:    */       catch (IllegalArgumentException e)
/*  39:    */       {
/*  40: 44 */         e.printStackTrace();
/*  41:    */       }
/*  42:    */       catch (IllegalAccessException e)
/*  43:    */       {
/*  44: 45 */         e.printStackTrace();
/*  45:    */       }
/*  46:    */     }
/*  47:    */   }
/*  48:    */   
/*  49:    */   private String[] getFields(String s)
/*  50:    */   {
/*  51: 50 */     String[] tmp = s.split(",");
/*  52: 52 */     for (int i = 0; i < tmp.length; i++) {
/*  53: 53 */       tmp[i] = tmp[i].trim();
/*  54:    */     }
/*  55: 55 */     return tmp;
/*  56:    */   }
/*  57:    */   
/*  58:    */   private void getOutputFields()
/*  59:    */   {
/*  60: 60 */     String[] outputFieldNames = getFields(this.outputFields);
/*  61: 62 */     if ((outputFieldNames == null) || (outputFieldNames[0].length() == 0)) {
/*  62: 62 */       return;
/*  63:    */     }
/*  64: 65 */     Field field = null;
/*  65:    */     
/*  66: 67 */     this.output = new ArrayList();
/*  67: 69 */     for (int i = 0; i < outputFieldNames.length; i++)
/*  68:    */     {
/*  69:    */       try
/*  70:    */       {
/*  71: 72 */         field = getClass().getDeclaredField(outputFieldNames[i]);
/*  72:    */       }
/*  73:    */       catch (SecurityException e)
/*  74:    */       {
/*  75: 73 */         e.printStackTrace();
/*  76:    */       }
/*  77:    */       catch (NoSuchFieldException e)
/*  78:    */       {
/*  79: 74 */         e.printStackTrace();
/*  80:    */       }
/*  81:    */       try
/*  82:    */       {
/*  83: 77 */         this.output.add(field.get(this));
/*  84:    */       }
/*  85:    */       catch (IllegalArgumentException e)
/*  86:    */       {
/*  87: 78 */         e.printStackTrace();
/*  88:    */       }
/*  89:    */       catch (IllegalAccessException e)
/*  90:    */       {
/*  91: 79 */         e.printStackTrace();
/*  92:    */       }
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public Object preprocess(Object... params)
/*  97:    */     throws GlobalException
/*  98:    */   {
/*  99: 88 */     setInputFields(params);
/* 100:    */     
/* 101:    */ 
/* 102: 91 */     execute();
/* 103:    */     
/* 104:    */ 
/* 105: 94 */     getOutputFields();
/* 106: 96 */     if ((this.output == null) || (this.output.size() == 0)) {
/* 107: 97 */       return null;
/* 108:    */     }
/* 109: 99 */     return this.output.get(0);
/* 110:    */   }
/* 111:    */   
/* 112:    */   public Object preprocessAll(Object... params)
/* 113:    */     throws GlobalException
/* 114:    */   {
/* 115:107 */     setInputFields(params);
/* 116:    */     
/* 117:    */ 
/* 118:110 */     execute();
/* 119:    */     
/* 120:    */ 
/* 121:113 */     getOutputFields();
/* 122:115 */     if ((this.output == null) || (this.output.size() == 0)) {
/* 123:116 */       return null;
/* 124:    */     }
/* 125:118 */     return this.output;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public abstract void execute()
/* 129:    */     throws GlobalException;
/* 130:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.Algorithm
 * JD-Core Version:    0.7.0.1
 */