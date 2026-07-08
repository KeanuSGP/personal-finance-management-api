import { MdOutlineKeyboardArrowDown } from "react-icons/md";
import '../Select/Select.css'
import { useEffect, useRef, useState } from "react";
import { api } from '../../services/api'

const Item = ({ style, onClick, content, id, dataType, dataId}) => {
    return (
        <>
            <div className={style} onClick={onClick} id={id} key={id} data-type={dataType} data-id={dataId}>
                {content}
            </div>
        </>
    )
}

const Select = ({ name, data, handleChange, itemName, value }) => {

    const element = useRef(null);

    const [active, setActive] = useState(false);
    const [selected, setSelected] = useState(value != undefined ? value : null);
    
    const handleActive = () => {
        if (active) return setActive(!active)
        setActive(true);
    }

    const handleSelected = (e) => {
        if (data.length <= 0) return setSelected(null);
        setSelected(e);
        setActive(false);
    }

    window.addEventListener("click", (e) => {
        if (element.current == null) return;
        if (!element.current.contains(e.target)) setActive(false);
    })

    return (
        <>
            <div className="selectContainer" ref={element}>
                <div className="selectExternalPart" onClick={handleActive}>
                    <p className={`${selected !== null ?'' : "pSize"}`}>{selected !== null ? selected : 'Selecione'}</p>
                    <MdOutlineKeyboardArrowDown className="iconSize" />
                </div>
                <div className={`${active ? 'activeSelect' : ''} selectMainPart`}>
                    {
                        data !== undefined ? (data.length > 0 ? (data.map((item) => (
                            <Item
                                style={'option'}
                                onClick={(e) => {handleSelected(item.name); handleChange(e)}}
                                content={item.name}
                                id={item.name}
                                key={item.name}
                                dataType={itemName}
                                dataId={item.id}
                            />
                        ))) : (<p>No data found</p>)) : (<p>No data found</p>)
                    }
                </div>
            </div>
        </>
    )

}

export default Select;