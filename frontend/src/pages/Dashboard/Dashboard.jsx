import React from "react";
import "./Dashboard.css";
import PopularStocks from "../../components/PopularStocks/PopularStocks";
import Bist100Card from "../../components/Bist100/Bist100";
import DailyOrderCard from "../../components/DailyOrderCard/DailyOrderCard";
import TotalTradeVolumeCard from "../../components/TotalTradeVolumeCard/TotalTradeVolumeCard";
import ActiveCustomerCard from "../../components/ActiveCustomerCard/ActiveCustomerCard";
import RecentTransactions from "../../components/RecentTransactions/RecentTransactions";

const Dashboard = () => {
  return (
    <div className="dashboard">
      <h1 className="title">Hoş Geldiniz</h1>

      <div className="summaryCards">
        <Bist100Card className="card" />
        <DailyOrderCard className="card" />
        <TotalTradeVolumeCard className="card" />
        <ActiveCustomerCard className="card" />
      </div>

      <div className="bottomSection">
        <PopularStocks className="noMaxWidth" />
        <RecentTransactions />
      </div>
    </div>
  );
};

export default Dashboard;
