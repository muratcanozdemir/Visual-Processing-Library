/*  1:   */ package vpt.algorithms.texture.alot;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import weka.classifiers.Evaluation;
/*  5:   */ import weka.classifiers.lazy.IB1;
/*  6:   */ 
/*  7:   */ public class ClassifierTest
/*  8:   */ {
/*  9:   */   public static void main(String[] args)
/* 10:   */   {
/* 11:43 */     String testPath = "test.arff";
/* 12:45 */     for (int i = 1; i <= 1000; i++)
/* 13:   */     {
/* 14:46 */       String trainPath = "train" + i + ".arff";
/* 15:47 */       String[] options = { "-t", trainPath, "-T", testPath, "-o" };
/* 16:   */       try
/* 17:   */       {
/* 18:50 */         String results = Evaluation.evaluateModel(new IB1(), options);
/* 19:51 */         int index = results.indexOf("Correctly Classified Instances");
/* 20:52 */         results = results.substring(index + 100);
/* 21:   */         
/* 22:54 */         index = results.indexOf("Correctly Classified Instances");
/* 23:55 */         results = results.substring(index);
/* 24:   */         
/* 25:57 */         index = results.indexOf("%");
/* 26:58 */         results = results.substring(55, index);
/* 27:59 */         System.err.println(results);
/* 28:   */       }
/* 29:   */       catch (Exception e)
/* 30:   */       {
/* 31:61 */         e.printStackTrace();
/* 32:   */       }
/* 33:   */     }
/* 34:   */   }
/* 35:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.alot.ClassifierTest
 * JD-Core Version:    0.7.0.1
 */