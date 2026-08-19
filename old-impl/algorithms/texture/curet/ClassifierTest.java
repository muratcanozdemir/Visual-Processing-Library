/*  1:   */ package vpt.algorithms.texture.curet;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import weka.classifiers.Evaluation;
/*  5:   */ import weka.classifiers.lazy.IB1;
/*  6:   */ 
/*  7:   */ public class ClassifierTest
/*  8:   */ {
/*  9:   */   public static void main(String[] args)
/* 10:   */   {
/* 11:43 */     for (int i = 1; i <= 100; i++)
/* 12:   */     {
/* 13:44 */       String trainPath = "train" + i + ".arff";
/* 14:45 */       String testPath = "test" + i + ".arff";
/* 15:   */       
/* 16:47 */       String[] options = { "-t", trainPath, "-T", testPath, "-o" };
/* 17:   */       try
/* 18:   */       {
/* 19:50 */         String results = Evaluation.evaluateModel(new IB1(), options);
/* 20:51 */         int index = results.indexOf("Correctly Classified Instances");
/* 21:52 */         results = results.substring(index + 100);
/* 22:   */         
/* 23:54 */         index = results.indexOf("Correctly Classified Instances");
/* 24:55 */         results = results.substring(index);
/* 25:   */         
/* 26:57 */         index = results.indexOf("%");
/* 27:58 */         results = results.substring(55, index);
/* 28:59 */         System.err.println(results);
/* 29:   */       }
/* 30:   */       catch (Exception e)
/* 31:   */       {
/* 32:61 */         e.printStackTrace();
/* 33:   */       }
/* 34:   */     }
/* 35:   */   }
/* 36:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.curet.ClassifierTest
 * JD-Core Version:    0.7.0.1
 */