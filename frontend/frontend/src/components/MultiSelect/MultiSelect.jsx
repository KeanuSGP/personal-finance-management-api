import { CiSearch } from "react-icons/ci";
import './MultiSelect.css'
import { useEffect, useState, useRef } from "react";
import { api } from '../../services/api'
import { IoIosCloseCircle } from "react-icons/io";

const Item = ({ value, style, unmark, dataId }) => {
    return (
        <>
            <div className={style} onClick={unmark} data-type='categories' data-id={dataId}>
                {value}
                <IoIosCloseCircle />
            </div>
        </>
    )
}

const MultiSelect = ({ options, handleSelect, value }) => {

    const [active, setActive] = useState(false);
    const [selectedItems, setSelectedItems] = useState(value != null ? value : []);
    const selectedItemsToRender = selectedItems.slice(0, 2);
    const selectedItemsNotRenderized = selectedItems.length - 2;
    const [filteredItems, setFilteredItems] = useState([]);
    const ref = useRef(null);

    const handleActive = () => {
        if (active) return setActive(false);
        setActive(true);
    }

    const handleSelectedItem = (e) => {
        if (selectedItems.length) {
            return setSelectedItems(prev => prev.some(selected => selected.id == e.target.dataset.id) ? selectedItems : [ ...prev, {id: parseInt(e.target.dataset.id), name: e.target.textContent }])
        }

        setSelectedItems(prev => ([
            ...prev,
        {
            id: parseInt(e.target.dataset.id),
            name: e.target.textContent
        }
        ]))

    }

    const handleUnmarkItem = (e) => {
        const unmarked = e
        const arr = selectedItems.filter(value => value.name != e.currentTarget.textContent)
        handleSelect(e, 'delete');
        setSelectedItems([...arr]);
    }

    const handleSearch = (e) => {
        setFilteredItems(options.filter(option => option.name.toLowerCase().includes(e.toLowerCase())))
    }

    const handleOutsideClick = (e) => {
        if (!ref.current?.contains(e.target)) setActive(false);
    }

    useEffect(() => {
        document.addEventListener("click", handleOutsideClick)
        return () => {
            document.removeEventListener("click", handleOutsideClick);
        };
    }, [])
    return (
        <>
            <div className="multiSelectBody" ref={ref}>
                <div className="multiSelectHeader">
                    <div className="items" style={selectedItems?.length ? {} : { display: "none" }}>
                        {selectedItems?.length ?
                            (
                                selectedItems?.length > 2 ?
                                    (
                                        selectedItemsToRender.map((item) => (
                                            <Item value={item.name} dataId={item.id} style={'itemStyle'} key={item.id} unmark={(e) => handleUnmarkItem(e)} />
                                        ))
                                    ) : (
                                        selectedItems.map((item, index) => (
                                            <Item value={item.name} dataId={item.id} style={'itemStyle'} key={item.id} unmark={(e) => handleUnmarkItem(e)} />
                                        ))
                                    )
                            ) : ("")}
                        <p className="notRenderizedItemsP" style={selectedItems?.length > 2 ? { visibility: 'visible' } : { visibility: 'hidden' }}>{selectedItems.length > 2 ? `+${selectedItemsNotRenderized}` : ''}</p>
                    </div>
                    <div className="search">
                        <input type="text" placeholder="Selecione" onChange={(e) => handleSearch(e.target.value)} onClick={handleActive}></input>
                        <CiSearch />
                    </div>
                </div>
                <div className={`${active ? 'active' : ''} multiSelectMain`}>
                    {
                        options?.length ?
                            (
                                filteredItems?.length ? (
                                    filteredItems.map((option, index) => (
                                        <p data-type="categories" data-id={option.id} onClick={(e) => {
                                            handleSelectedItem(e)
                                            handleSelect(e, 'add')
                                        }} key={index}>{option.name}</p>
                                    ))
                                ) : (options.map(option => (
                                    <p data-type="categories" data-id={option.id} onClick={(e) => {
                                        handleSelectedItem(e)
                                        handleSelect(e, 'add')
                                    }} key={option.name}>{option.name}</p>
                                )))
                            ) : (<p>No data found</p>)
                    }
                </div>
            </div>
        </>
    )
}

export default MultiSelect