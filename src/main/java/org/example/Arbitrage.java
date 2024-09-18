package org.example;

public class Arbitrage implements Comparable<Arbitrage> {
    private final Double PRIZE_PICKS_ODDS = 3.0;
    private final Double PRIZE_PICKS_WAGER = 5.;
    private final String player;
    private final String bet365Pick;
    private final double bet365Odds;
    private final String market;
    private Double bet365Wager;
    private Double guaranteedPayout;

    public Arbitrage(String player, String market, String bet365Pick, Double bet365Odds) {
        this.player = player;
        this.market = market;
        this.bet365Pick = bet365Pick;
        this.bet365Odds = bet365Odds > 0 ? 1 + (bet365Odds/100) : 1-(100/bet365Odds);
        this.getArbitrageWager();
        int g = 3;
    }

    private void getArbitrageWager() {
        double bet365ArbitragePercentage = 1 / this.bet365Odds;
        double prizePicksArbtragePercentage = 1 / this.PRIZE_PICKS_ODDS;
        double totalArbitragePercentage = bet365ArbitragePercentage + prizePicksArbtragePercentage;
        if(totalArbitragePercentage < 100) {
            double totalWager = (PRIZE_PICKS_WAGER * totalArbitragePercentage) / prizePicksArbtragePercentage;
            this.bet365Wager = (totalWager * bet365ArbitragePercentage)/totalArbitragePercentage;
            this.guaranteedPayout = (totalWager/totalArbitragePercentage)-totalWager;
        }
    }

    public String getPlayer() {
        return player;
    }

    public String getBet365Pick() {
        return bet365Pick;
    }

    public double getBet365Odds() {
        return bet365Odds;
    }

    public Double getBet365Wager() {
        return bet365Wager;
    }

    public Double getGuaranteedPayout() {
        return guaranteedPayout;
    }

    @Override
    public int compareTo(Arbitrage o) {
        return o.getGuaranteedPayout().compareTo(this.getGuaranteedPayout());
    }

    public void print() {
        System.out.println(String.format("%s, %s,%s (%.2f) $%.2f,$%.2f", market, player, bet365Pick, bet365Odds, bet365Wager, guaranteedPayout));
    }
}
