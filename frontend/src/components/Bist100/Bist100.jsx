import styles from './Bist100.module.css';
import useBist100Data from '../../hooks/useBist100Data';
import UpArrow from '../Icons/UpArrow';
import DownArrow from '../Icons/DownArrow';

function Bist100({ className }) {

const data = useBist100Data();

  if (!data) {
    return <div className={styles.loading}>Yükleniyor…</div>;
  }

  const { current, changerate } = data;
  const positive = changerate >= 0;

  const formattedCurrent = current.toFixed(3).replace(/\./g, '');
  const formattedChange  = changerate.toFixed(2) + '%';
  
  return (
    <div className={`${styles.container} ${className || ''}`}>
      <h4 className={styles.title}>BIST 100</h4>
      <div className={styles.content}>
        <div className={styles.current}>
          {formattedCurrent}
        </div>
        <div className={`${styles.change} ${positive ? styles.positive : styles.negative}`}>
          {positive
            ? <UpArrow className={styles.icon}/>
            : <DownArrow className={styles.icon}/>
          }
          <span className={styles.rateText}>
            {changerate.toFixed(2)}%
          </span>
        </div>
      </div>
    </div>
  );
}

export default Bist100;
