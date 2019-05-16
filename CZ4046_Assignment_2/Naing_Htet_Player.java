class Naing_Htet_Player extends Player {
   int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

       // Rule 1: our agent will cooperate in the first round
       if (n == 0)  {
           return 0;
       }

       // Rule 2: our agent will defect in the last few rounds, NastyPlayer mode is turned on
       if (n > 95) {
           return 1;
       }

       // Rule 3: if all players including our agent cooperated in the previous round,
       // then our agent will continue to cooperate
       if (myHistory[n-1] == 0 && oppHistory1[n-1] == 0 && oppHistory2[n-1] == 0) {
           return 0;
       }

       // Rule 4: check opponents history to see if they have defected before
       for (int i = 0; i < n; i++) {
           if (oppHistory1[i] == 1 || oppHistory2[i] == 1) {
               // if either one of them defected before, our agent will always defect
               return 1;
           }
       }
       // Rule 5: Otherwise, by default nature, our agent will always cooperate
       return 0;
   }
}
