import './Card.css'


const Card = ({ id, name, label, labelValue, selectCard, isSelected, color }) => {
    if (color != undefined) {
        const isHex = (str) => /^[0-9a-fA-F]{0,6}$/.test(str);
        const hex = color.replace('', '#');
    }

    return (

        <div className={`cardContainer ${isSelected ? 'selected' : ''}`} id={id} onClick={selectCard}>
            <div className='header'>
                <h1 className='entityTitle'>{name}</h1>
                {/* <div className={`${isHex(color) ? 'color-circle' : 'none-display'}`} style={{ backgroundColor: isHex ? hex : '' }}></div> */}
            </div>
            <div className='entityData'>
                <p>{label}: {labelValue}</p>
                {/* <p>Payable Transactions: {payable}</p>
                <p>Receivable Transactions: {receivable}</p> */}
            </div>
        </div>
    )
}

export default Card