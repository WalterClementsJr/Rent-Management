USE [NhaTro]
GO
/****** Object:  User [dummy]    Script Date: 2022-04-29 20:25:12 ******/
CREATE USER [dummy] FOR LOGIN [dummy] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  DatabaseRole [db_executor]    Script Date: 2022-04-29 20:25:12 ******/
CREATE ROLE [db_executor]
GO
ALTER ROLE [db_executor] ADD MEMBER [dummy]
GO
ALTER ROLE [db_datareader] ADD MEMBER [dummy]
GO
ALTER ROLE [db_datawriter] ADD MEMBER [dummy]
GO
/****** Object:  UserDefinedFunction [dbo].[GetLatestPayDate]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetLatestPayDate](@mahdong int)
RETURNS DATE
AS
BEGIN
	DECLARE @d date
	set @d =
	(
		select max(hdon.ngaytt) from hoadon hdon
		where ngaytt =
		(
			select max(ngaytt) from hoadon where mahdong=@mahdong
		)
	)
	return @d
END
GO
/****** Object:  UserDefinedFunction [dbo].[GetMaHoaDonCuNhat]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetMaHoaDonCuNhat](@mahdong int)
RETURNS int
AS
begin
declare @id int
	set @id = 
	(
		select hdon.MAHDON from hoadon hdon
		where ngaytt =
		(
			select min(ngaytt) from hoadon where mahdong=@mahdong
		)
	)
	return @id
END
GO
/****** Object:  UserDefinedFunction [dbo].[GetMaHoaDonMoiNhat]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetMaHoaDonMoiNhat](@mahdong int)
RETURNS int
AS
begin
declare @id int
	set @id = 
	(
		select max(hdon.MAHDON) from hoadon hdon
		where ngaytt =
		(
			select max(ngaytt) from hoadon where mahdong=@mahdong
		)
	)
	return @id
END
GO
/****** Object:  UserDefinedFunction [dbo].[GetMaKhuTuPhong]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetMaKhuTuPhong](@maphong INT)
RETURNS INT
AS
BEGIN
	DECLARE @makhu int;
	set @makhu = 
	(
		select khu.MAKHU from khu
		join phong on khu.makhu=phong.makhu
		where phong.maphong=@maphong
	)

	RETURN @makhu
END
GO
/****** Object:  UserDefinedFunction [dbo].[GetPhiBaoTriTrongNamCuaKhu]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create function [dbo].[GetPhiBaoTriTrongNamCuaKhu](@makhu int, @ngay date)
returns decimal(19,3)
as
begin
declare @tien decimal(19,3)
set @tien = 
(
	select sum(chiphi)
	from
	(
		select b.*, k.makhu
		from BAOTRI b
		join phong p on p.maphong=b.maphong
		join khu k on k.makhu=p.makhu
	) as tableA
	where tablea.makhu=@makhu
	and year(@ngay) = year(tableA.NGAY)
)
if @tien is null
	begin return 0 end
return @tien
end
GO
/****** Object:  UserDefinedFunction [dbo].[GetPhiBaoTriTrongThangCuaKhu]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create function [dbo].[GetPhiBaoTriTrongThangCuaKhu](@makhu int, @ngay date)
returns decimal(19,3)
as
begin
declare @tien decimal(19,3)
set @tien = 
(
	select sum(chiphi)
	from
	(
		select b.*, k.makhu
		from BAOTRI b
		join phong p on p.maphong=b.maphong
		join khu k on k.makhu=p.makhu
	) as tableA
	where tablea.makhu=@makhu
	and year(@ngay) = year(tableA.NGAY) and month(@ngay) = month(tableA.ngay)
)
if @tien is null
	begin return 0 end
return @tien
end
GO
/****** Object:  UserDefinedFunction [dbo].[getsonguoitrongkhu]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

create function [dbo].[getsonguoitrongkhu](@makhu int)
returns int
as
begin

declare @b as int
declare @sokhach int
--set @sokhach = (select dbo.getsonguoitrongphong(@makhu))
set @sokhach =
(
	select sum(dbo.getsonguoitrongphong(tablea.maphong))
	from (select p.maphong from khu k join phong p on p.makhu=k.makhu where k.makhu=@makhu) as tablea
)

return @sokhach
end
GO
/****** Object:  UserDefinedFunction [dbo].[GetSoNguoiTrongPhong]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE FUNCTION [dbo].[GetSoNguoiTrongPhong](@maphong INT)
RETURNS INT
AS
begin
	declare @sokhach int
	set @sokhach = 
	(
	select count(*) as songuoi from 
		(
			select * from GetKhachTrongPhong(@maphong)
		) as atable
	)
	return @sokhach
end
GO
/****** Object:  UserDefinedFunction [dbo].[GetTongTienCuaKhu]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE function [dbo].[GetTongTienCuaKhu](@makhu int)
returns decimal(19,3)
as
begin
declare @tien decimal(19,3)
set @tien = 
(
	select sum(sotien)
	from
	(
		select hd.mahdong, dbo.GetMaKhuTuPhong(hd.MAPHONG) as makhu, hdon.sotien, hdon.NGAYDONG
		from hoadon hdon
		join hopdong hd
		on hdon.MAHDONG=hd.mahdong
	) as tableA
	where tablea.makhu=@makhu
	--and year(@ngay) = year(tableA.NGAYDONG) and month(@ngay) = month(tableA.NGAYDONG)
)
if @tien is null
	begin return 0 end
return @tien
end
GO
/****** Object:  UserDefinedFunction [dbo].[GetTongTienTrongNam]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE function [dbo].[GetTongTienTrongNam](@ngay date)
returns decimal(19,3)
as
begin
declare @tien decimal(19,3)
set @tien = 
(
	select sum(SOTIEN) from hoadon
	where year(@ngay) = year(hoadon.NGAYDONG)
)
if @tien is null
	begin return 0 end
return @tien
end
GO
/****** Object:  UserDefinedFunction [dbo].[GetTongTienTrongNamCuaKhu]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE function [dbo].[GetTongTienTrongNamCuaKhu](@makhu int, @ngay date)
returns decimal(19,3)
as
begin
declare @tien decimal(19,3)
set @tien = 
(
	select sum(sotien)
	from
	(
		select hd.mahdong, dbo.GetMaKhuTuPhong(hd.MAPHONG) as makhu, hdon.sotien, hdon.NGAYDONG
		from hoadon hdon
		join hopdong hd
		on hdon.MAHDONG=hd.mahdong
	) as tableA
	where tablea.makhu=@makhu
	and year(@ngay) = year(tableA.NGAYDONG)
)

if @tien is null
	begin return 0 end
return @tien
end
GO
/****** Object:  UserDefinedFunction [dbo].[GetTongTienTrongThang]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE function [dbo].[GetTongTienTrongThang](@ngay date)
returns decimal(19,3)
as
begin
declare @tien decimal(19,3)
set @tien = 
(
	select sum(SOTIEN) from hoadon
	where month(@ngay) = month(hoadon.NGAYDONG) and year(@ngay) = year(hoadon.NGAYDONG)
)
if @tien is null
	begin return 0 end
return @tien
end
GO
/****** Object:  UserDefinedFunction [dbo].[GetTongTienTrongThangCuaKhu]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE function [dbo].[GetTongTienTrongThangCuaKhu](@makhu int, @ngay date)
returns decimal(19,3)
as
begin
declare @tien decimal(19,3)
set @tien = 
(
	select sum(sotien)
	from
	(
		select hd.mahdong, dbo.GetMaKhuTuPhong(hd.MAPHONG) as makhu, hdon.sotien, hdon.NGAYDONG
		from hoadon hdon
		join hopdong hd
		on hdon.MAHDONG=hd.mahdong
	) as tableA
	where tablea.makhu=@makhu
	and year(@ngay) = year(tableA.NGAYDONG) and month(@ngay) = month(tableA.NGAYDONG)
)
if @tien is null
	begin return 0 end
return @tien
end
GO
/****** Object:  UserDefinedFunction [dbo].[IsHopDongEndable]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE function [dbo].[IsHopDongEndable](@mahd int)
returns bit
as
begin
if exists (select 1 from ViewHopDongVaExtraInfo where songay > 0 and mahdong=@mahd) return cast(0 as bit)
else return cast(1 as bit)

return cast(0 as bit)
end
GO
/****** Object:  UserDefinedFunction [dbo].[IsKhachDeletable]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create function [dbo].[IsKhachDeletable](@makh int)
returns bit
as
begin
	IF EXISTS (SELECT * FROM hopdong WHERE MAKH = @makh)
		return 0
	else IF EXISTS ( SELECT * FROM HOPDONG_KHACH WHERE MAKH = @makh)
		return 0
	else return 1

	return 0
end
GO
/****** Object:  UserDefinedFunction [dbo].[IsKhuDeletable]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create function [dbo].[IsKhuDeletable](@makhu int)
returns bit
as
begin
	IF EXISTS ( SELECT * FROM phong WHERE MAKHU = @makhu)
		return 0
	else return 1
	return 0
end
GO
/****** Object:  UserDefinedFunction [dbo].[IsPhongCoHopDongTrong]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[IsPhongCoHopDongTrong](@mahdong int, @maphong int, @start date, @end date)
RETURNS bit
AS
BEGIN
	DECLARE @isOverlapped bit
	set @isOverlapped =
	(
		iif
		(
			exists
			(
				select 1 from hopdong
				where MAPHONG=@maphong and mahdong!=@mahdong
				and ngaytra >= @start
				and ngaynhan <= @end
			), 1, 0
		 )
	)
	return @isOverlapped
END
GO
/****** Object:  UserDefinedFunction [dbo].[IsPhongConCho]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create FUNCTION [dbo].[IsPhongConCho](@maphong int)
RETURNS bit
AS
BEGIN
	DECLARE @current int
	DECLARE @max int
	DECLARE @isAvailable bit
	set @max = (select songuoi from phong where maphong=@maphong)
	set @current = (select * from GetSoKhachTrongPhong(2))
	
	if @current >= @max return 0
	else return 1

	return 0
END
GO
/****** Object:  UserDefinedFunction [dbo].[IsPhongDeletable]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create function [dbo].[IsPhongDeletable](@maphong int)
returns bit
as
begin
	IF EXISTS (SELECT * FROM hopdong WHERE maphong = @maphong)
		return 0
	--else IF EXISTS ( SELECT * FROM HOPDONG_KHACH WHERE MAKH = @phong)
	--	return 0
	else return 1

	return 0
end
GO
/****** Object:  UserDefinedFunction [dbo].[IsPhongFull]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE function [dbo].[IsPhongFull](@maphong int)
returns bit
as
begin

declare @b as bit
declare @sokhach int
set @sokhach = (select dbo.getsonguoitrongphong(@maphong))
set @b =
(
select cast
	(
		case when @sokhach >= phong.songuoi then 1
		else 0
		end as bit
	) from PHONG where maphong = @maphong
)

return @b
end
GO
/****** Object:  Table [dbo].[PHONG]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PHONG](
	[MAPHONG] [int] IDENTITY(1,1) NOT NULL,
	[TENPHONG] [nvarchar](50) NOT NULL,
	[SONGUOI] [smallint] NOT NULL,
	[MAKHU] [int] NOT NULL,
	[GIAGOC] [decimal](19, 3) NOT NULL,
	[TIENCOC] [decimal](19, 3) NOT NULL,
	[DIENTICH] [int] NOT NULL,
	[MOTA] [nvarchar](200) NULL,
 CONSTRAINT [PK_PHONG] PRIMARY KEY CLUSTERED 
(
	[MAPHONG] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[KHU]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[KHU](
	[MAKHU] [int] IDENTITY(1,1) NOT NULL,
	[TENKHU] [nvarchar](50) NOT NULL,
	[DIACHI] [nvarchar](200) NULL,
 CONSTRAINT [PK_KHU_1] PRIMARY KEY CLUSTERED 
(
	[MAKHU] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[HOPDONG]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HOPDONG](
	[MAHDONG] [int] IDENTITY(1,1) NOT NULL,
	[MAPHONG] [int] NOT NULL,
	[MAKH] [int] NOT NULL,
	[NGAYNHAN] [date] NOT NULL,
	[NGAYTRA] [date] NOT NULL,
	[TIENCOC] [decimal](19, 3) NOT NULL,
 CONSTRAINT [PK_HOPDONG] PRIMARY KEY CLUSTERED 
(
	[MAHDONG] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[KHACH]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[KHACH](
	[MAKH] [int] IDENTITY(1,1) NOT NULL,
	[HOTEN] [nvarchar](50) NOT NULL,
	[GIOITINH] [bit] NOT NULL,
	[NGAYSINH] [date] NOT NULL,
	[SDT] [char](12) NOT NULL,
	[CMND] [char](12) NOT NULL,
 CONSTRAINT [PK_KHACK] PRIMARY KEY CLUSTERED 
(
	[MAKH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  View [dbo].[ViewHopDongVaInfo]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE view [dbo].[ViewHopDongVaInfo]
as 
select hd.mahdong, k.makhu, k.tenkhu, p.MAPHONG, p.tenphong, kh.makh, kh.hoten, hd.ngaynhan, hd.ngaytra, hd.TIENCOC,p.GIAGOC
from hopdong hd
join phong p on p.maphong=hd.maphong
join khu k on k.makhu=p.makhu
join khach kh on kh.makh=hd.makh
GO
/****** Object:  View [dbo].[ViewHoaDonVaExtraInfo]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create view [dbo].[ViewHoaDonVaExtraInfo]
as
SELECT 
A.mahdong, A.makhu, A.tenkhu, A.MAPHONG, A.tenphong, A.makh, A.hoten, A.ngaynhan, A.ngaytra, A.TIENCOC, A.GIAGOC, A.ngayttgannhat,
CASE WHEN a.ngaytra <= getdate()
	THEN DATEDIFF(DAY, A.ngayttgannhat, a.ngaytra) 
	ELSE DATEDIFF(DAY, A.ngayttgannhat, getdate()) END AS songay,
dbo.getmahoadonmoinhat(A.mahdong) as mahdon

FROM            (SELECT        mahdong, makhu, tenkhu, MAPHONG, tenphong, makh, hoten, ngaynhan, ngaytra, TIENCOC, GIAGOC, dbo.GetLatestPayDate(mahdong) AS ngayttgannhat
                          FROM            dbo.ViewHopDongVaInfo AS x) AS A INNER JOIN
                         dbo.ViewHopDongVaInfo AS vhd ON A.mahdong = vhd.mahdong
GO
/****** Object:  Table [dbo].[HOPDONG_KHACH]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HOPDONG_KHACH](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[MAHDONG] [int] NOT NULL,
	[MAKH] [int] NOT NULL,
	[NGAYNHAN] [date] NOT NULL,
	[NGAYTRA] [date] NOT NULL,
 CONSTRAINT [PK_HOPDONG_KHACH] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  View [dbo].[ViewKhachCoPhong]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[ViewKhachCoPhong]
AS
SELECT DISTINCT k.MAKH, k.HOTEN, k.GIOITINH, k.NGAYSINH, k.SDT, k.CMND
FROM            dbo.KHACH AS k INNER JOIN
                         dbo.HOPDONG_KHACH AS hdk ON k.MAKH = hdk.MAKH
WHERE        (hdk.NGAYTRA > GETDATE()) OR
                         (hdk.NGAYTRA IS NULL)
GO
/****** Object:  View [dbo].[ViewKhachCu]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[ViewKhachCu]
AS
SELECT        MAKH, HOTEN, GIOITINH, NGAYSINH, SDT, CMND
FROM            (SELECT DISTINCT k.MAKH, k.HOTEN, k.GIOITINH, k.NGAYSINH, k.SDT, k.CMND
                          FROM            (SELECT        MAKH, MAX(NGAYTRA) AS ngtra
                                                    FROM            dbo.HOPDONG_KHACH AS hdk
                                                    WHERE        (NGAYTRA <= GETDATE())
                                                    GROUP BY MAKH) AS t1 INNER JOIN
                                                    dbo.KHACH AS k ON t1.MAKH = k.MAKH) AS A
WHERE        (NOT EXISTS
                             (SELECT        MAKH, HOTEN, GIOITINH, NGAYSINH, SDT, CMND
                               FROM            dbo.ViewKhachCoPhong AS vkcp
                               WHERE        (A.MAKH = MAKH)))
GO
/****** Object:  View [dbo].[ViewKhachKhongCoPhong]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE view [dbo].[ViewKhachKhongCoPhong]
as
select * from khach
where not exists
(	select 1 from
	(
		select * from ViewKhachCoPhong
	)  as A where KHACH.makh=A.makh
)
GO
/****** Object:  UserDefinedFunction [dbo].[GetPhongCoNguoiThuocKhu]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetPhongCoNguoiThuocKhu] (@makhu INT)
RETURNS TABLE
AS
RETURN
	select distinct p.* from phong p
	join hopdong hd on hd.MAPHONG=p.maphong
	join HOPDONG_KHACH hdk on hdk.MAHDONG=hd.MAHDONG
	where hd.NGAYTRA > GETDATE() and p.MAKHU=@makhu
GO
/****** Object:  UserDefinedFunction [dbo].[GetPhongTrongThuocKhu]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetPhongTrongThuocKhu] (@makhu INT)
RETURNS TABLE
AS
RETURN
	select p.* from phong p
	where not exists
	(
		select * from GetPhongCoNguoiThuocKhu(@makhu) as t where t.MAPHONG=p.MAPHONG
	)
	and p.makhu=@makhu
GO
/****** Object:  UserDefinedFunction [dbo].[GetMaHopDongTuPhongCoKhach]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetMaHopDongTuPhongCoKhach](@maphong INT)
RETURNS TABLE
AS
RETURN
	select hd.mahdong from 
	(
		select * from GetPhongCoNguoiThuocKhu(dbo.getmakhutuphong(@maphong))
	) as p
	join hopdong hd on hd.MAPHONG=p.maphong
	where hd.NGAYTRA > GETDATE() AND p.maphong=@maphong
GO
/****** Object:  View [dbo].[ViewHopDongVaExtraInfo]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE view [dbo].[ViewHopDongVaExtraInfo]
as
SELECT        A.*, CASE WHEN a.ngaytra <= getdate() THEN DATEDIFF(DAY, A.ngayttgannhat, a.ngaytra) ELSE DATEDIFF(DAY, A.ngayttgannhat, getdate()) 
                         END AS songay
FROM (SELECT  *, dbo.GetLatestPayDate(MAHDONG) AS ngayttgannhat
                          FROM dbo.ViewHopDongVaInfo AS x) AS A
						 INNER JOIN dbo.ViewHopDongVaInfo AS vhd ON A.MAHDONG = vhd.MAHDONG
GO
/****** Object:  View [dbo].[ViewKhachOGhepVaExtraInfo]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE view [dbo].[ViewKhachOGhepVaExtraInfo]
as
select * from
(
SELECT        hdk.ID, hdk.MAHDONG, hdinfo.makhu, hdinfo.tenkhu, hdinfo.MAPHONG, hdinfo.tenphong, hdinfo.makh as machu, hdinfo.hoten as tenchu, hdk.MAKH AS makhoghep, k.HOTEN AS tenkhoghep, hdinfo.ngaynhan, hdinfo.ngaytra, 
                         hdk.NGAYNHAN AS ngayvao, hdk.NGAYTRA AS ngaydi
FROM            dbo.HOPDONG_KHACH AS hdk INNER JOIN
                         dbo.ViewHopDongVaInfo AS hdinfo ON hdinfo.mahdong = hdk.MAHDONG INNER JOIN
                         dbo.KHACH AS k ON k.MAKH = hdk.MAKH
) as info
WHERE  (info.machu != info.makhoghep)
GO
/****** Object:  Table [dbo].[BAOTRI]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BAOTRI](
	[MABAOTRI] [int] IDENTITY(1,1) NOT NULL,
	[MAPHONG] [int] NOT NULL,
	[CHIPHI] [decimal](19, 3) NOT NULL,
	[NGAY] [date] NOT NULL,
	[MOTA] [nvarchar](200) NOT NULL,
 CONSTRAINT [PK_BAOTRI] PRIMARY KEY CLUSTERED 
(
	[MABAOTRI] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[GetBaoTriThuocKhu]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE function [dbo].[GetBaoTriThuocKhu](@makhu int)
returns table
as return
select bt.mabaotri,bt.MAPHONG, p.tenphong, bt.CHIPHI,bt.NGAY, bt.mota from baotri bt
join phong p on bt.maphong=p.maphong
join khu k on k.makhu=p.makhu
where k.makhu=@makhu
GO
/****** Object:  UserDefinedFunction [dbo].[GetKhachTrongPhong]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetKhachTrongPhong](@maphong INT)
RETURNS TABLE
AS
RETURN
	select k.*
	from (select * from HOPDONG_KHACH where NGAYTRA>getdate()) as hdk
	join khach k on k.makh=hdk.makh
	join hopdong as hd on hd.MAHDONG=hdk.MAHDONG
	join phong p on p.MAPHONG=hd.MAPHONG
	where p.MAPHONG=@maphong
GO
/****** Object:  UserDefinedFunction [dbo].[GetPhongThuocKhu]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetPhongThuocKhu] (@makhu INT)
RETURNS TABLE
AS
RETURN
	select p.* from phong p
	join khu k on k.makhu=p.makhu
	where p.makhu=@makhu
GO
/****** Object:  UserDefinedFunction [dbo].[GetSoKhachTrongPhong]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetSoKhachTrongPhong](@maphong INT)
RETURNS TABLE
AS
RETURN
	select count(*) as songuoi from 
	(
		select k.*
		from (select * from HOPDONG_KHACH where NGAYTRA>getdate()) as hdk
		join khach k on k.makh=hdk.makh
		join hopdong as hd on hd.MAHDONG=hdk.MAHDONG
		join phong p on p.MAPHONG=hd.MAPHONG
		where p.MAPHONG=@maphong
	) as a
GO
/****** Object:  View [dbo].[ViewHopDongConHieuLuc]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[ViewHopDongConHieuLuc] AS
select * from hopdong where NGAYTRA > GETDATE()
GO
/****** Object:  View [dbo].[ViewPhongCoNguoi]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[ViewPhongCoNguoi] AS
select distinct p.* from phong p
join hopdong hd on hd.MAPHONG=p.maphong
join HOPDONG_KHACH hdk on hdk.MAHDONG=hd.MAHDONG
where hd.NGAYTRA > GETDATE()
GO
/****** Object:  View [dbo].[ViewPhongTrong]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[ViewPhongTrong] AS
select p.* from phong p
where not exists
(
	select 1 from hopdong hd
	join HOPDONG_KHACH hdk on hdk.MAHDONG=hd.MAHDONG
	--join PHONG p on p.MAPHONG=hd.MAPHONG
	where p.MAPHONG=hd.MAPHONG and hd.NGAYTRA > GETDATE()
)
GO
/****** Object:  Table [dbo].[HOADON]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HOADON](
	[MAHDON] [int] IDENTITY(1,1) NOT NULL,
	[MAHDONG] [int] NOT NULL,
	[SOTIEN] [decimal](19, 3) NOT NULL,
	[NGAYTT] [date] NOT NULL,
	[NGAYDONG] [date] NOT NULL,
 CONSTRAINT [PK_HDON] PRIMARY KEY CLUSTERED 
(
	[MAHDON] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
GRANT ALTER ON [dbo].[HOADON] TO [dummy] AS [dbo]
GO
ALTER TABLE [dbo].[HOADON] ADD  CONSTRAINT [DF_HOADON_VAONGAY]  DEFAULT (getdate()) FOR [NGAYDONG]
GO
ALTER TABLE [dbo].[BAOTRI]  WITH CHECK ADD  CONSTRAINT [FK_BAOTRI_PHONG] FOREIGN KEY([MAPHONG])
REFERENCES [dbo].[PHONG] ([MAPHONG])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[BAOTRI] CHECK CONSTRAINT [FK_BAOTRI_PHONG]
GO
ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_HOPDONG] FOREIGN KEY([MAHDONG])
REFERENCES [dbo].[HOPDONG] ([MAHDONG])
GO
ALTER TABLE [dbo].[HOADON] CHECK CONSTRAINT [FK_HOADON_HOPDONG]
GO
ALTER TABLE [dbo].[HOPDONG]  WITH CHECK ADD  CONSTRAINT [FK_HOPDONG_KHACH] FOREIGN KEY([MAKH])
REFERENCES [dbo].[KHACH] ([MAKH])
GO
ALTER TABLE [dbo].[HOPDONG] CHECK CONSTRAINT [FK_HOPDONG_KHACH]
GO
ALTER TABLE [dbo].[HOPDONG]  WITH CHECK ADD  CONSTRAINT [FK_HOPDONG_PHONG] FOREIGN KEY([MAPHONG])
REFERENCES [dbo].[PHONG] ([MAPHONG])
GO
ALTER TABLE [dbo].[HOPDONG] CHECK CONSTRAINT [FK_HOPDONG_PHONG]
GO
ALTER TABLE [dbo].[HOPDONG_KHACH]  WITH CHECK ADD  CONSTRAINT [FK_HOPDONG_KHACH_HOPDONG] FOREIGN KEY([MAHDONG])
REFERENCES [dbo].[HOPDONG] ([MAHDONG])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[HOPDONG_KHACH] CHECK CONSTRAINT [FK_HOPDONG_KHACH_HOPDONG]
GO
ALTER TABLE [dbo].[HOPDONG_KHACH]  WITH CHECK ADD  CONSTRAINT [FK_HOPDONG_KHACH_KHACH] FOREIGN KEY([MAKH])
REFERENCES [dbo].[KHACH] ([MAKH])
GO
ALTER TABLE [dbo].[HOPDONG_KHACH] CHECK CONSTRAINT [FK_HOPDONG_KHACH_KHACH]
GO
ALTER TABLE [dbo].[PHONG]  WITH CHECK ADD  CONSTRAINT [FK_PHONG_KHU] FOREIGN KEY([MAKHU])
REFERENCES [dbo].[KHU] ([MAKHU])
GO
ALTER TABLE [dbo].[PHONG] CHECK CONSTRAINT [FK_PHONG_KHU]
GO
ALTER TABLE [dbo].[HOPDONG]  WITH CHECK ADD  CONSTRAINT [CK_HOPDONG_NGAYNHAN_NGAYTRA] CHECK  (([NGAYTRA]>=[NGAYNHAN]))
GO
ALTER TABLE [dbo].[HOPDONG] CHECK CONSTRAINT [CK_HOPDONG_NGAYNHAN_NGAYTRA]
GO
ALTER TABLE [dbo].[HOPDONG_KHACH]  WITH CHECK ADD  CONSTRAINT [CK_HOPDONG_KHACH_NGAYNHAN_NGAYTRA] CHECK  (([NGAYTRA]>=[NGAYNHAN]))
GO
ALTER TABLE [dbo].[HOPDONG_KHACH] CHECK CONSTRAINT [CK_HOPDONG_KHACH_NGAYNHAN_NGAYTRA]
GO
/****** Object:  Trigger [dbo].[TriggerCheckBeforeDeleteHoaDon]    Script Date: 2022-04-29 20:25:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[TriggerCheckBeforeDeleteHoaDon]
ON [dbo].[HOADON]
AFTER DELETE
AS
begin
	DECLARE @mahdoncunhat INT
	DECLARE @mahdoncanxoa INT
	Declare @mahdong int

	select @mahdong = mahdong from deleted

	select @mahdoncunhat = dbo.GetMaHoaDonCuNhat(@mahdong)
	--(select @mahdoncanxoa = mahdon from deleted)

	--print ('mahdon cu nhat: ' + convert(varchar(4), @mahdoncunhat)  + ' mahdon canxoa' + convert(varchar(4), @mahdoncanxoa) )
	if (@mahdoncunhat is null)
	begin
		rollback tran;
	end
end
GO
ALTER TABLE [dbo].[HOADON] ENABLE TRIGGER [TriggerCheckBeforeDeleteHoaDon]
GO
/****** Object:  Trigger [dbo].[ADD_EMPTY_HOADON]    Script Date: 2022-04-29 20:25:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create trigger [dbo].[ADD_EMPTY_HOADON] on [dbo].[HOPDONG]
for insert
as
	insert into hoadon (mahdong, sotien, NGAYTT)
	select mahdong, 0, NGAYNHAN
		from inserted
GO
ALTER TABLE [dbo].[HOPDONG] ENABLE TRIGGER [ADD_EMPTY_HOADON]
GO
/****** Object:  Trigger [dbo].[TRIGGER_ADD_TO_HOPDONG_KHACH]    Script Date: 2022-04-29 20:25:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[TRIGGER_ADD_TO_HOPDONG_KHACH] ON [dbo].[HOPDONG]
FOR INSERT
AS

INSERT INTO HOPDONG_KHACH
        (MAHDONG, MAKH, NGAYNHAN, NGAYTRA)
    SELECT
        MAHDONG, MAKH, NGAYNHAN, NGAYTRA
        FROM inserted
GO
ALTER TABLE [dbo].[HOPDONG] ENABLE TRIGGER [TRIGGER_ADD_TO_HOPDONG_KHACH]
GO
/****** Object:  Trigger [dbo].[TRIGGER_UPDATE_HOPDONG_KHACH]    Script Date: 2022-04-29 20:25:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[TRIGGER_UPDATE_HOPDONG_KHACH] 
ON [dbo].[HOPDONG]  
instead of UPDATE
AS
BEGIN
	declare @ngay date
	set @ngay = (select ngaytra from hopdong where mahdong = (select max(mahdong) from inserted))
	print('ngay tra can so sanh' + cast(@ngay as varchar(30)))

	UPDATE HOPDONG_KHACH
    set hopdong_khach.NGAYTRA = inserted.NGAYTRA
	FROM HOPDONG_KHACH
	join inserted on inserted.MAHDONG=HOPDONG_KHACH.mahdong
	where hopdong_khach.NGAYTRA = @ngay

	update hopdong set hopdong.ngaytra=inserted.ngaytra, hopdong.tiencoc=inserted.tiencoc
	from inserted
	where hopdong.mahdong=inserted.mahdong
END
GO
ALTER TABLE [dbo].[HOPDONG] ENABLE TRIGGER [TRIGGER_UPDATE_HOPDONG_KHACH]
GO
/****** Object:  Trigger [dbo].[TriggerDeleteFromHoaDon]    Script Date: 2022-04-29 20:25:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create TRIGGER [dbo].[TriggerDeleteFromHoaDon]
ON [dbo].[HOPDONG]
instead of DELETE
AS
begin
	DECLARE @mahdong INT

	alter table hoadon
	DISABLE TRIGGER TriggerCheckBeforeDeleteHoaDon

	set @mahdong = (select mahdong from deleted )

	delete from hoadon where hoadon.mahdong = @mahdong

	delete from hopdong where MAHDONG = @mahdong

	alter table hoadon
	enable TRIGGER triggercheckbeforedeletehoadon

end
GO
ALTER TABLE [dbo].[HOPDONG] ENABLE TRIGGER [TriggerDeleteFromHoaDon]
GO
/****** Object:  Trigger [dbo].[TRIGGER_INSERT_AUTO_SET_NGAYTRA]    Script Date: 2022-04-29 20:25:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[TRIGGER_INSERT_AUTO_SET_NGAYTRA] ON [dbo].[HOPDONG_KHACH]
FOR INSERT
AS
BEGIN
	 update HOPDONG_KHACH
	 set NGAYTRA =
	(
		select hopdong.NGAYTRA from hopdong
		where HOPDONG.MAHDONG = i.MAHDONG
	)
	 from inserted i
	 join HOPDONG_KHACH hdk on hdk.MAHDONG = i.MAHDONG
	 where i.NGAYTRA is null
END
GO
ALTER TABLE [dbo].[HOPDONG_KHACH] ENABLE TRIGGER [TRIGGER_INSERT_AUTO_SET_NGAYTRA]
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Ngày trả luôn lớn hơn ngày nhận' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'HOPDONG', @level2type=N'CONSTRAINT',@level2name=N'CK_HOPDONG_NGAYNHAN_NGAYTRA'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Ngày trả luôn lớn hơn ngày nhận' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'HOPDONG_KHACH', @level2type=N'CONSTRAINT',@level2name=N'CK_HOPDONG_KHACH_NGAYNHAN_NGAYTRA'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPane1', @value=N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "hd"
            Begin Extent = 
               Top = 6
               Left = 38
               Bottom = 136
               Right = 208
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "p"
            Begin Extent = 
               Top = 6
               Left = 246
               Bottom = 136
               Right = 416
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "k"
            Begin Extent = 
               Top = 138
               Left = 38
               Bottom = 251
               Right = 208
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "kh"
            Begin Extent = 
               Top = 138
               Left = 246
               Bottom = 268
               Right = 416
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
      Begin ColumnWidths = 9
         Width = 284
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'ViewHopDongVaInfo'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPaneCount', @value=1 , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'ViewHopDongVaInfo'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPane1', @value=N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "k"
            Begin Extent = 
               Top = 6
               Left = 38
               Bottom = 136
               Right = 208
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "hdk"
            Begin Extent = 
               Top = 6
               Left = 246
               Bottom = 136
               Right = 416
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'ViewKhachCoPhong'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPaneCount', @value=1 , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'ViewKhachCoPhong'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPane1', @value=N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "A"
            Begin Extent = 
               Top = 6
               Left = 38
               Bottom = 136
               Right = 208
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'ViewKhachCu'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPaneCount', @value=1 , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'ViewKhachCu'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPane1', @value=N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "hdk"
            Begin Extent = 
               Top = 6
               Left = 38
               Bottom = 136
               Right = 208
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "hdinfo"
            Begin Extent = 
               Top = 6
               Left = 246
               Bottom = 136
               Right = 416
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "k"
            Begin Extent = 
               Top = 138
               Left = 38
               Bottom = 268
               Right = 208
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
      Begin ColumnWidths = 9
         Width = 284
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'ViewKhachOGhepVaExtraInfo'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPaneCount', @value=1 , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'ViewKhachOGhepVaExtraInfo'
GO
