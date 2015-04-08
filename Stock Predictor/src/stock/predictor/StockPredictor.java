/* Original work by K. K. Agarwal
 * Stock predictor Java conversion
 * Converted by Ryan Hammontree
 * June 12, 2013 1:10pm - 2:55pm
 * June 13, 2013 1:50pm - 2:36pm
 * June 19, 2013 1:10pm - 2:10pm
 * June 19, 2013 5:05pm - 5:28pm
 */

package stock.predictor;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Scanner;
import java.text.NumberFormat;


public class StockPredictor {

   
    public static void main(String[] args) {
        
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the stock symbol of interest:");
        String symbol = scan.nextLine();
        System.out.println("Please enter the number of days of history (5 to 50):");
        int numDays = scan.nextInt();
        double numDaysStored = numDays;
        
        // Get a currency formatter for the current locale.
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        NumberFormat percfmt = NumberFormat.getPercentInstance();
        double HistoryTrend[] = new double[numDays-1]; //will store close amounts
        int count = 0; //reusable counter
        //Format of the downloaded histroy table is:
        //Date          Open	High	Low	Close	Volume	       Adj Close (Including splits)
        //Jun 4, 2013	453.22	454.43	447.39	449.31	10,421,200	449.31
        //Jun 3, 2013	450.73	452.36	442.48	450.72	13,298,300	450.72
        //May 31, 2013	452.50	457.10	449.50	449.73	13,725,100	449.73

        try
        {
            URL url = new URL("http://finance.yahoo.com/q/hp?s="
                    +symbol+"+Historical+Prices");
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(url.openStream()));
            
            String line;
            String page = "";
            while ((line = reader.readLine()) != null)
            {
                page += line;
            }
                    
        // Throw away stuff from front of table with data
        int begin=page.indexOf(("tabledata"));
        int end=page.indexOf ("Close price adjusted for dividends and splits.");
        String prices= page.substring(begin+1, end+1);
        // print(prices)
        System.out.println("Date\t\tOpen\tHigh\tLow\tClose\tVolume\t\t"
                + "Adj Close(Including splits)");
        
        String oldDate = "None";
        
        // Loop for numDays of history 
            for(int i=1; i < numDays; i++)
            {
            int dateBegin = prices.indexOf("right")+5;
            prices = prices.substring(dateBegin+2);
            //  print(prices[0:50])
            int dateEnd = prices.indexOf("<");
            //   print(dateBegin)
            //   print(dateEnd)
            String date = prices.substring(0, dateEnd);
            
            if (date != oldDate)
            {
                System.out.println(" ");
                prices = prices.substring(dateEnd+10);
                System.out.print(date + "\t");
                oldDate = date;
                
                for(int j=1;j < 7; j++)
                {
                    int closePriceBegin = prices.indexOf(">");
                    prices = prices.substring(closePriceBegin+1);
                    int closePriceEnd = prices.indexOf("</td");
                    String currentPrice = prices.substring(0, closePriceEnd);
                    System.out.print(currentPrice + "\t");
                    
                    if (j == 4)
                    {
                        HistoryTrend[count] = Double.parseDouble(currentPrice);
                        count++;
                    }
                            
                    prices = prices.substring(closePriceEnd+5);
                        
                }
            }
            else
                numDays = numDays-1;
            }
            System.out.println("\n\n");
        int sumPositiveTrends = 0;
        int sumNegativeTrends = 0;
        double valuePositiveTrend = 0.0;
        double valueNegativeTrend = 0.0;
        double runningPT = 0.0;
        double runningNT = 0.0;
        count = 0;        
        for (double value: HistoryTrend)
        {
            count++;
            if (count == HistoryTrend.length)
                break;
            if (value > HistoryTrend[count])
            {
                sumPositiveTrends++;
                valuePositiveTrend = value - HistoryTrend[count];
                runningPT += valuePositiveTrend;
                System.out.println(fmt.format(valuePositiveTrend)); 
            }
            else
            {
                sumNegativeTrends++;
                valueNegativeTrend = HistoryTrend[count] - value ;
                runningNT += valueNegativeTrend;
                System.out.println("-" + fmt.format(valueNegativeTrend));
            }
        }
            System.out.println("Positive: " + sumPositiveTrends + "   " 
                    + "Negative: " + sumNegativeTrends);
            System.out.println("Total Positive: " + fmt.format(runningPT) + "   " 
                    + "Total Negative: -" + fmt.format(runningNT));
            
        double positiveAvg = 0.0;
        double negativeAvg = 0.0;
        
        if (sumPositiveTrends != 0)
            positiveAvg = runningPT/sumPositiveTrends;
        if (sumNegativeTrends != 0)
            negativeAvg = runningNT/sumNegativeTrends;
        
        
        double positiveChance = sumPositiveTrends/numDaysStored;
        double negativeChance = sumNegativeTrends/numDaysStored;
        System.out.println("There is a " + percfmt.format(positiveChance)
                + " chance for an increase in the amount of " 
                + fmt.format(positiveAvg));
        System.out.println("There is a " + percfmt.format(negativeChance)
                + " chance for a decrease in the amount of " 
                + fmt.format(negativeAvg));
                
        reader.close();
        }   
        
        catch (MalformedURLException e)
        {
            System.out.println("Invalid URL");
        }
        catch (IOException e)
        {
            System.out.println("Invalid Input");
        }
         
    }

}


# K. K. Agarwal
# Stock predictor
# Start: 5/31/13, 10:20 am
import urllib.request

#Format of the downloaded histroy table is:
#Date	Open	High	Low	Close	Volume	Adj Close (Including splits)
#Jun 4, 2013	453.22	454.43	447.39	449.31	10,421,200	449.31
#Jun 3, 2013	450.73	452.36	442.48	450.72	13,298,300	450.72
#May 31, 2013	452.50	457.10	449.50	449.73	13,725,100	449.73
symbol=input("Please enter the stock symbol of interest:")
numDays=int(input("Please enter the number of days of history (5 to 50):"))
page = str(urllib.request.urlopen("http://finance.yahoo.com/q/hp?s="+symbol+"+Historical+Prices").read())
#print (page[0:100])

# Throw away stuff from front of table with data
begin=page.find("tabledata")
end=page.find("Close price adjusted for dividends and splits.")
prices=page[begin:end+1]
#print(prices)
print("Date\t\tOpen\tHigh\tLow\tClose\tVolume\t\tAdj Close(Including splits)")

oldDate="None"
historyTable=[]
# Loop for numDays of history             
for i in range (1,numDays+1):
    dateBegin=prices.find("right")+5
    prices=prices[dateBegin+2:]
#   print(prices[0:50])
    dateEnd=prices.find("<")
#   print(dateBegin)
#   print(dateEnd)
    date=prices[0:dateEnd]
    if date !=oldDate:
        print(" ")
        prices=prices[dateEnd+10:]
        print(date,'\t',end='')
        oldDate=date
        for j in range (1,7):
            closePriceBegin=prices.find(">")
            prices=prices[closePriceBegin+1:]
            closePriceEnd=prices.find("</td")
            closePrice=prices[0:closePriceEnd]
            print(closePrice,'\t',end='')
            if j==4:
                historyTable.append((date,float(closePrice)))
            prices=prices[closePriceEnd+5:]
    else:
        numDays=numDays-1
        
# Print the important part of the historyTable
print(" ")
#for i in range(0, numDays):
#    print(historyTable[i][0], historyTable [i][1])

# Find the probabilty of the stock going Up or Down this day based on past performance
sumPositiveTrends=0
sumNegativeTrends=0
valuePositiveTrends=0.0
valueNegativeTrends=0.0
for index in range (0, numDays-1):
    if historyTable[index][0]!=historyTable[index+1][0]:
# Ignore the row if it has the same date (for dividend declarations)
        if historyTable[index][1]>historyTable[index+1][1]:
            sumPositiveTrends=sumPositiveTrends+1
            valuePositiveTrends=valuePositiveTrends+historyTable[index][1]-historyTable[index+1][1]
            print("Value Positive=","${0:.2f}".format(valuePositiveTrends))
        else:
            if historyTable[index][1]<historyTable[index+1][1]:
                sumNegativeTrends=sumNegativeTrends+1
                valueNegativeTrends=valueNegativeTrends+historyTable[index+1][1]-historyTable[index][1]
                print("Value Negative=","${0:.2f}".format(valueNegativeTrends))

print("Positive=",sumPositiveTrends, "Negative=",sumNegativeTrends)
print("Value Positive=","${0:.2f}".format(valuePositiveTrends),"Value Negative=","${0:.2f}".format(valueNegativeTrends))

# Compute averages
positiveAverage=0.0
negativeAvg=0.0

if sumPositiveTrends!=0:
    positiveAverage=valuePositiveTrends/sumPositiveTrends

if sumNegativeTrends!=0:
    negativeAverage=valueNegativeTrends/sumNegativeTrends
print("There is a","{0:.4f}".format(sumPositiveTrends/numDays),"chance for an increase in the amount of","${0:.2f}".format(positiveAverage))
print("There is a","{0:.4f}".format(sumNegativeTrends/numDays),"chance for a decrease in the amount of","${0:.2f}".format(negativeAverage))
*/