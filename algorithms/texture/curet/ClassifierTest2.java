/*  1:   */ package vpt.algorithms.texture.curet;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import weka.classifiers.Evaluation;
/*  5:   */ import weka.classifiers.lazy.IB1;
/*  6:   */ 
/*  7:   */ public class ClassifierTest2
/*  8:   */ {
/*  9:   */   public static void main(String[] args)
/* 10:   */   {
/* 11:43 */     String testPath = "test.arff";
/* 12:45 */     for (int i = 1; i <= 100; i++)
/* 13:   */     {
/* 14:46 */       String trainPath = "train" + i + ".arff";
/* 15:   */       
/* 16:48 */       String[] options = { "-t", trainPath, "-T", testPath, "-o" };
/* 17:   */       try
/* 18:   */       {
/* 19:51 */         String results = Evaluation.evaluateModel(new IB1(), options);
/* 20:52 */         int index = results.indexOf("Correctly Classified Instances");
/* 21:53 */         results = results.substring(index + 100);
/* 22:   */         
/* 23:55 */         index = results.indexOf("Correctly Classified Instances");
/* 24:56 */         results = results.substring(index);
/* 25:   */         
/* 26:58 */         index = results.indexOf("%");
/* 27:59 */         results = results.substring(55, index);
/* 28:60 */         System.err.println(results);
/* 29:   */       }
/* 30:   */       catch (Exception e)
/* 31:   */       {
/* 32:62 */         e.printStackTrace();
/* 33:   */       }
/* 34:   */     }
/* 35:   */   }
/* 36:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.curet.ClassifierTest2
 * JD-Core Version:    0.7.0.1
 */