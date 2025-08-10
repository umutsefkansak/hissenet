import useBist100Data from '../../hooks/useBist100Data';
import UpArrow from '../Icons/UpArrow';
import DownArrow from '../Icons/DownArrow';
import DashboardCard from '../common/Card/DashboardCard';

function Bist100() {

const data = useBist100Data();
  
   if (!data) {
    return (
      <DashboardCard
        title="BIST 100"
        icon={null}
        value="Yükleniyor…"
        subtitle=""
      />
    );
  }
  
  const { current, changerate } = data;
  const positive = changerate >= 0;
  const formattedCurrent = `${current.toFixed(3).replace(/\./g, '')}₺`;
  const formattedRate  = `${changerate.toFixed(2)}%`;
  const variant        = positive ? "up-arrow" : "down-arrow"
  const iconComponent  = positive ? <UpArrow width={24} height={24} /> : <DownArrow width={24} height={24} />;

  return (

    <DashboardCard
      title="BIST 100"
      icon={iconComponent}
      value={formattedCurrent}
      subtitle={formattedRate}
      iconVariant={variant}
    />
  );
}
export default Bist100;
