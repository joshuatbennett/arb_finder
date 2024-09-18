package bettingprosscraper;


import org.example.Arbitrage;

public class Bet {
    private final String market;
    private final String player;
    private final Double bet365Line;
    private final Double prizePicksLine;
    private final Double overOdds;
    private final Double underOdds;
    private final Arbitrage underArbitrage;
    private final Arbitrage overArbitrage;
    private final Boolean sameLine;

    public Bet(Boolean sameLine, String market, String player, Double bet365Line, Double prizePicksLine, Double overOdds, Double underOdds) {
        this.market = market;
        this.player = player;
        this.bet365Line = bet365Line;
        this.prizePicksLine = prizePicksLine;
        this.overOdds = overOdds;
        this.underOdds = underOdds;
        this.underArbitrage = new Arbitrage(market, player, "O " + bet365Line, underOdds);
        this.overArbitrage = new Arbitrage(market, player, "U " + bet365Line, overOdds);
        this.sameLine = sameLine;
    }

    public String getMarket() {
        return market;
    }

    public String getPlayer() {
        return player;
    }

    public Double getBet365Line() {
        return bet365Line;
    }

    public Double getOverOdds() {
        return overOdds;
    }

    public Double getUnderOdds() {
        return underOdds;
    }

    public Arbitrage getUnderArbitrage() {
        return underArbitrage;
    }

    public Arbitrage getOverArbitrage() {
        return overArbitrage;
    }

    public boolean getSameLine() {
        return this.sameLine;
    }
}
